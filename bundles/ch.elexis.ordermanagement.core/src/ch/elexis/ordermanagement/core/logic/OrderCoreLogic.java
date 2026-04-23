package ch.elexis.ordermanagement.core.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IOrder;
import ch.elexis.core.model.IOrderEntry;
import ch.elexis.core.model.IOutputLog;
import ch.elexis.core.model.IStock;
import ch.elexis.core.model.IStockEntry;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.OrderEntryState;
import ch.elexis.core.services.IOrderService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.StockServiceHolder;

public class OrderCoreLogic {

	private static final Logger logger = LoggerFactory.getLogger(OrderCoreLogic.class);
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //$NON-NLS-1$

	public static List<IOrder> getOpenOrders() {
		return getOrders(false, true);
	}

	public static List<IOrder> getCompletedOrders(boolean showAllYears) {
		return getOrders(true, showAllYears);
	}

	private static List<IOrder> getOrders(boolean completed, boolean showAllYears) {
		IQuery<IOrder> query = CoreModelServiceHolder.get().getQuery(IOrder.class);
		List<IOrder> orders = query.execute();
		if (!showAllYears) {
			LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
			orders = orders.stream().filter(order -> {
				LocalDateTime orderTimestamp = order.getTimestamp();
				return orderTimestamp != null && orderTimestamp.isAfter(twoYearsAgo);
			}).collect(Collectors.toList());
		}
		return orders.stream()
				.filter(order -> (completed && order.isDone() && !order.getEntries().isEmpty())
						|| (!completed && (!order.isDone() || order.getEntries().isEmpty())))
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp())).collect(Collectors.toList());
	}

	public static IOrder createOrder(String name, IOrderService orderService) {
		IOrder order = CoreModelServiceHolder.get().create(IOrder.class);
		order.setTimestamp(LocalDateTime.now());
		order.setName(name);
		CoreModelServiceHolder.get().save(order);
		orderService.getHistoryService().logCreateOrder(order);
		return order;
	}

	public static void saveSingleDelivery(IOrderEntry entry, int partialDelivery, IOrderService orderService) {
		if (entry == null || partialDelivery == 0) {
			return;
		}

		try {
			int orderAmount = entry.getAmount();
			int currentDelivered = entry.getDelivered();
			int newDelivered = currentDelivered + partialDelivery;

			if (newDelivered < 0) {
				newDelivered = 0;
			}

			IStock stock = entry.getStock();
			if (stock != null) {
				updateStockEntry(stock, entry, partialDelivery);
			}
			orderService.getHistoryService().logDelivery(entry.getOrder(), entry, newDelivered, orderAmount);
			entry.setDelivered(newDelivered);
			if (newDelivered >= entry.getAmount()) {
				entry.setState(OrderEntryState.DONE);
			} else if (newDelivered > 0) {
				entry.setState(OrderEntryState.PARTIAL_DELIVER);
			} else {
				entry.setState(OrderEntryState.ORDERED);
			}
			CoreModelServiceHolder.get().save(entry);
			IOrder order = entry.getOrder();
			boolean allDelivered = order.getEntries().stream().allMatch(e -> e.getState() == OrderEntryState.DONE);
			if (allDelivered) {
				orderService.getHistoryService().logCompleteDelivery(order);
			}

		} catch (NumberFormatException e) {
			logger.error("Error: Invalid partialDelivery value: " + partialDelivery, e); //$NON-NLS-1$
		}
	}

	public static void saveAllDeliveries(List<IOrderEntry> entries, IOrderService orderService) {
		for (IOrderEntry entry : entries) {
			int partialDelivery = entry.getAmount() - entry.getDelivered();
			if (partialDelivery > 0) {
				saveSingleDelivery(entry, partialDelivery, orderService);
			}
		}
	}

	public static IOrder addItemsToExistingOrder(IOrder actOrder, List<IArticle> articlesToOrder, IOrderService orderService) {
		if (actOrder == null) {
			return null;
		}
		
		for (IArticle article : articlesToOrder) {
			int quantity = 1;

			String mandatorId = ContextServiceHolder.get().getActiveMandator().map(IMandator::getId).orElse(null);
			IStock currentStock = StockServiceHolder.get().getMandatorDefaultStock(mandatorId);

			Optional<IOrderEntry> existingEntry = actOrder.getEntries().stream().filter(
					e -> e.getArticle().equals(article) && e.getStock() != null && e.getStock().equals(currentStock))
					.findFirst();

			if (existingEntry.isPresent()) {
				IOrderEntry orderEntry = existingEntry.get();
				int oldQuantity = orderEntry.getAmount();
				int newQuantity = oldQuantity + quantity;
				orderEntry.setAmount(newQuantity);
				CoreModelServiceHolder.get().save(orderEntry);

				orderService.getHistoryService().logEdit(actOrder, orderEntry, oldQuantity, newQuantity);
			} else {
				IStock stock = StockServiceHolder.get().getMandatorDefaultStock(mandatorId);
				IOrderEntry newOrderEntry = actOrder.addEntry(article, stock, null, quantity);
				orderService.getHistoryService().logChangedAmount(actOrder, newOrderEntry, 0, quantity);
				CoreModelServiceHolder.get().save(newOrderEntry);
			}
		}
		return actOrder;
	}

	public static String formatDate(LocalDateTime dateTime) {
		if(dateTime == null) return "";
		return dateTime.format(FORMATTER);
	}

	public static IOrder getSelectedOrder(String orderId, boolean isCompleted) {
		IQuery<IOrder> query = CoreModelServiceHolder.get().getQuery(IOrder.class);
		return query.execute().stream()
				.filter(o -> o.getId().equals(orderId) && (o.isDone() == isCompleted || o.getEntries().isEmpty()))
				.findFirst().orElse(null);
	}

	public static IOutputLog getOrderLogEntry(IOrder order) {
		if (order == null) {
			return null;
		}
		IQuery<IOutputLog> query = CoreModelServiceHolder.get().getQuery(IOutputLog.class);
		query.and(ModelPackage.Literals.IOUTPUT_LOG__OBJECT_ID, COMPARATOR.EQUALS, order.getId());
		return query.execute().isEmpty() ? null : query.execute().get(0);
	}

	public static void updateStockEntry(IStock stock, IOrderEntry entry, int amountToAdd) {
		if (stock == null || entry == null || entry.getArticle() == null) {
			logger.error("Error: Invalid parameters in updateStockEntry()"); //$NON-NLS-1$
			return;
		}

		Optional<IStockEntry> existingStockEntry = stock.getStockEntries().stream()
				.filter(se -> se.getArticle().equals(entry.getArticle())).findFirst();

		if (existingStockEntry.isPresent()) {
			IStockEntry se = existingStockEntry.get();
			int current = se.getCurrentStock();
			int newStock = current + amountToAdd;
			if (newStock < 0) {
				newStock = 0;
			}
			se.setCurrentStock(newStock);
			CoreModelServiceHolder.get().save(se);
		} else {
			int startStock = Math.max(0, amountToAdd);
			IStockEntry newStockEntry = CoreModelServiceHolder.get().create(IStockEntry.class);
			newStockEntry.setArticle(entry.getArticle());
			newStockEntry.setStock(stock);
			newStockEntry.setCurrentStock(startStock);
			CoreModelServiceHolder.get().save(newStockEntry);
		}
	}

	public static boolean isOrderCompletelyDelivered(IOrder order) {
		if (order == null || order.getEntries().isEmpty()) {
			return false;
		}
		return order.getEntries().stream().allMatch(e -> e.getState() == OrderEntryState.DONE);
	}
}