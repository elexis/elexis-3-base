var FC = $.fullCalendar; // a reference to FullCalendar's root namespace
var View = FC.View;      // the class that all views must inherit from
var ParallelView;

var lastRightClickEvent;

/* FullCalendar-specific DOM Utilities
----------------------------------------------------------------------------------------------------------------------*/


// Given the scrollbar widths of some other container, create borders/margins on rowEls in order to match the left
// and right space that was offset by the scrollbars. A 1-pixel border first, then margin beyond that.
function compensateScroll(rowEls, scrollbarWidths) {
	if (scrollbarWidths.left) {
		rowEls.css({
			'border-left-width': 1,
			'margin-left': scrollbarWidths.left - 1
		});
	}
	if (scrollbarWidths.right) {
		rowEls.css({
			'border-right-width': 1,
			'margin-right': scrollbarWidths.right - 1
		});
	}
}


// Undoes compensateScroll and restores all borders/margins
function uncompensateScroll(rowEls) {
	rowEls.css({
		'margin-left': '',
		'margin-right': '',
		'border-left-width': '',
		'border-right-width': ''
	});
}


// Make the mouse cursor express that an event is not allowed in the current area
function disableCursor() {
	$('body').addClass('fc-not-allowed');
}


// Returns the mouse cursor to its original look
function enableCursor() {
	$('body').removeClass('fc-not-allowed');
}


// Given a total available height to fill, have `els` (essentially child rows) expand to accomodate.
// By default, all elements that are shorter than the recommended height are expanded uniformly, not considering
// any other els that are already too tall. if `shouldRedistribute` is on, it considers these tall rows and 
// reduces the available height.
function distributeHeight(els, availableHeight, shouldRedistribute) {

	// *FLOORING NOTE*: we floor in certain places because zoom can give inaccurate floating-point dimensions,
	// and it is better to be shorter than taller, to avoid creating unnecessary scrollbars.

	var minOffset1 = Math.floor(availableHeight / els.length); // for non-last element
	var minOffset2 = Math.floor(availableHeight - minOffset1 * (els.length - 1)); // for last element *FLOORING NOTE*
	var flexEls = []; // elements that are allowed to expand. array of DOM nodes
	var flexOffsets = []; // amount of vertical space it takes up
	var flexHeights = []; // actual css height
	var usedHeight = 0;

	undistributeHeight(els); // give all elements their natural height

	// find elements that are below the recommended height (expandable).
	// important to query for heights in a single first pass (to avoid reflow oscillation).
	els.each(function (i, el) {
		var minOffset = i === els.length - 1 ? minOffset2 : minOffset1;
		var naturalOffset = $(el).outerHeight(true);

		if (naturalOffset < minOffset) {
			flexEls.push(el);
			flexOffsets.push(naturalOffset);
			flexHeights.push($(el).height());
		}
		else {
			// this element stretches past recommended height (non-expandable). mark the space as occupied.
			usedHeight += naturalOffset;
		}
	});

	// readjust the recommended height to only consider the height available to non-maxed-out rows.
	if (shouldRedistribute) {
		availableHeight -= usedHeight;
		minOffset1 = Math.floor(availableHeight / flexEls.length);
		minOffset2 = Math.floor(availableHeight - minOffset1 * (flexEls.length - 1)); // *FLOORING NOTE*
	}

	// assign heights to all expandable elements
	$(flexEls).each(function (i, el) {
		var minOffset = i === flexEls.length - 1 ? minOffset2 : minOffset1;
		var naturalOffset = flexOffsets[i];
		var naturalHeight = flexHeights[i];
		var newHeight = minOffset - (naturalOffset - naturalHeight); // subtract the margin/padding

		if (naturalOffset < minOffset) { // we check this again because redistribution might have changed things
			$(el).height(newHeight);
		}
	});
}


// Undoes distrubuteHeight, restoring all els to their natural height
function undistributeHeight(els) {
	els.height('');
}


// Given `els`, a jQuery set of <td> cells, find the cell with the largest natural width and set the widths of all the
// cells to be that width.
// PREREQUISITE: if you want a cell to take up width, it needs to have a single inner element w/ display:inline
function matchCellWidths(els) {
	var maxInnerWidth = 0;

	els.find('> *').each(function (i, innerEl) {
		var innerWidth = $(innerEl).outerWidth();
		if (innerWidth > maxInnerWidth) {
			maxInnerWidth = innerWidth;
		}
	});

	maxInnerWidth++; // sometimes not accurate of width the text needs to stay on one line. insurance

	els.width(maxInnerWidth);

	return maxInnerWidth;
}


// Given one element that resides inside another,
// Subtracts the height of the inner element from the outer element.
function subtractInnerElHeight(outerEl, innerEl) {
	var both = outerEl.add(innerEl);
	var diff;

	// effin' IE8/9/10/11 sometimes returns 0 for dimensions. this weird hack was the only thing that worked
	both.css({
		position: 'relative', // cause a reflow, which will force fresh dimension recalculation
		left: -1 // ensure reflow in case the el was already relative. negative is less likely to cause new scroll
	});
	diff = outerEl.outerHeight() - innerEl.outerHeight(); // grab the dimensions
	both.css({ position: '', left: '' }); // undo hack

	return diff;
}

/* A class for parallel agenda view. Each column is associated with a resource.
----------------------------------------------------------------------------------------------------------------------*/
// Is a manager for the resourceGrid subcomponent and possibly the DayGrid subcomponent (if allDaySlot is on).
// Responsible for managing width/height.

ParallelView = View.extend({

	scroller: null,

	resourceGrid: null, // the main time-grid subcomponent of this view

	axisWidth: null, // the width of the time axis running down the side

	headContainerEl: null, // div that hold's the resourceGrid's rendered date header
	noScrollRowEls: null, // set of fake row elements that must compensate when scroller has scrollbars

	// when the time-grid isn't tall enough to occupy the given height, we render an <hr> underneath
	bottomRuleEl: null,


	initialize: function () {
		this.resourceGrid = new FC.ResourceGrid(this);

		this.scroller = new FC.Scroller({
			overflowX: 'hidden',
			overflowY: 'auto'
		});
	},


	/* Rendering
	------------------------------------------------------------------------------------------------------------------*/


	// Sets the display range and computes all necessary dates
	setRange: function (range) {
		View.prototype.setRange.call(this, range); // call the super-method

		this.resourceGrid.setRange(range);
	},

	// Set the resource ids represented by columns 
	setResourceIds: function (resourceIds) {
		this.resourceGrid.setResourceIds(resourceIds);
		this.requestDateRender();
		this.requestCurrentEventsRender();
	},

	// Renders the view into `this.el`, which has already been assigned
	renderDates: function () {

		this.el.addClass('fc-agenda-view').html(this.renderSkeletonHtml());
		this.renderHead();

		this.scroller.render();
		var resourceGridWrapEl = this.scroller.el.addClass('fc-time-grid-container');
		var resourceGridEl = $('<div class="fc-time-grid" />').appendTo(resourceGridWrapEl);
		this.el.find('.fc-body > tr > td').append(resourceGridWrapEl);

		this.resourceGrid.setElement(resourceGridEl);
		this.resourceGrid.renderDates();

		// the <hr> that sometimes displays under the time-grid
		this.bottomRuleEl = $('<hr class="fc-divider ' + this.widgetHeaderClass + '"/>')
			.appendTo(this.resourceGrid.el); // inject it into the time-grid

		this.noScrollRowEls = this.el.find('.fc-row:not(.fc-scroller *)'); // fake rows not within the scroller

		this.registerRightclickListener();
	},

	registerRightclickListener: function () {
		var that = this;
		this.el.on('contextmenu', function (ev) {
			var fcContainer = $(ev.target).closest(
				'.fc-bg, .fc-slats, .fc-content-skeleton, ' +
				'.fc-bgevent-skeleton, .fc-highlight-skeleton'
			);
			var hit = that.queryHit(ev.pageX, ev.pageY);
			if (fcContainer.length) {
				that.prepareHits();
				cell = that.getHitSpan(hit);
			}
			if (cell) {
				that.triggerRightClick(
					that.getHitSpan(hit),
					that.getHitEl(hit),
					ev
				);
			}
		});
	},

	// render the day-of-week headers
	renderHead: function () {
		this.headContainerEl =
			this.el.find('.fc-head-container')
				.html(this.resourceGrid.renderHeadHtml());
	},


	// Unrenders the content of the view. Since we haven't separated skeleton rendering from date rendering,
	// always completely kill each grid's rendering.
	unrenderDates: function () {
		this.resourceGrid.unrenderDates();
		this.resourceGrid.removeElement();

		this.scroller.destroy();
	},


	// Builds the HTML skeleton for the view.
	// The day-grid and time-grid components will render inside containers defined by this HTML.
	renderSkeletonHtml: function () {
		return '' +
			'<table>' +
			'<thead class="fc-head">' +
			'<tr>' +
			'<td class="fc-head-container ' + this.widgetHeaderClass + '"></td>' +
			'</tr>' +
			'</thead>' +
			'<tbody class="fc-body">' +
			'<tr>' +
			'<td class="' + this.widgetContentClass + '">' +
			'</td>' +
			'</tr>' +
			'</tbody>' +
			'</table>';
	},


	// Generates an HTML attribute string for setting the width of the axis, if it is known
	axisStyleAttr: function () {
		if (this.axisWidth !== null) {
			return 'style="width:' + this.axisWidth + 'px"';
		}
		return '';
	},


	/* Business Hours
	------------------------------------------------------------------------------------------------------------------*/


	renderBusinessHours: function () {
		this.resourceGrid.renderBusinessHours();
	},


	unrenderBusinessHours: function () {
		this.resourceGrid.unrenderBusinessHours();
	},


	/* Now Indicator
	------------------------------------------------------------------------------------------------------------------*/


	getNowIndicatorUnit: function () {
		return this.resourceGrid.getNowIndicatorUnit();
	},


	renderNowIndicator: function (date) {
		this.resourceGrid.renderNowIndicator(date);
	},


	unrenderNowIndicator: function () {
		this.resourceGrid.unrenderNowIndicator();
	},


	/* Dimensions
	------------------------------------------------------------------------------------------------------------------*/


	updateSize: function (isResize) {
		this.resourceGrid.updateSize(isResize);

		View.prototype.updateSize.call(this, isResize); // call the super-method
	},


	// Refreshes the horizontal dimensions of the view
	updateWidth: function () {
		// make all axis cells line up, and record the width so newly created axis cells will have it
		this.axisWidth = matchCellWidths(this.el.find('.fc-axis'));
	},


	// Adjusts the vertical dimensions of the view to the specified values
	setHeight: function (totalHeight, isAuto) {
		var eventLimit;
		var scrollerHeight;
		var scrollbarWidths;

		// reset all dimensions back to the original state
		this.bottomRuleEl.hide(); // .show() will be called later if this <hr> is necessary
		this.scroller.clear(); // sets height to 'auto' and clears overflow
		uncompensateScroll(this.noScrollRowEls);


		if (!isAuto) { // should we force dimensions of the scroll container?

			scrollerHeight = this.computeScrollerHeight(totalHeight);
			this.scroller.setHeight(scrollerHeight);
			scrollbarWidths = this.scroller.getScrollbarWidths();

			if (scrollbarWidths.left || scrollbarWidths.right) { // using scrollbars?

				// make the all-day and header rows lines up
				compensateScroll(this.noScrollRowEls, scrollbarWidths);

				// the scrollbar compensation might have changed text flow, which might affect height, so recalculate
				// and reapply the desired height to the scroller.
				scrollerHeight = this.computeScrollerHeight(totalHeight);
				this.scroller.setHeight(scrollerHeight);
			}

			// guarantees the same scrollbar widths
			this.scroller.lockOverflow(scrollbarWidths);

			// if there's any space below the slats, show the horizontal rule.
			// this won't cause any new overflow, because lockOverflow already called.
			if (this.resourceGrid.getTotalSlatHeight() < scrollerHeight) {
				this.bottomRuleEl.show();
			}
		}
	},


	// given a desired total height of the view, returns what the height of the scroller should be
	computeScrollerHeight: function (totalHeight) {
		return totalHeight -
			subtractInnerElHeight(this.el, this.scroller.el); // everything that's NOT the scroller
	},


	/* Scroll
	------------------------------------------------------------------------------------------------------------------*/


	// Computes the initial pre-configured scroll state prior to allowing the user to change it
	computeInitialScroll: function () {
		var scrollTime = moment.duration(this.opt('scrollTime'));
		var top = this.resourceGrid.computeTimeTop(scrollTime);

		// zoom can give weird floating-point values. rather scroll a little bit further
		top = Math.ceil(top);

		if (top) {
			top++; // to overcome top border that slots beyond the first have. looks better
		}

		return { top: top };
	},


	queryScroll: function () {
		return { top: this.scroller.getScrollTop() };
	},


	setScroll: function (scroll) {
		this.scroller.setScrollTop(scroll.top);
	},


	/* Hit Areas
	------------------------------------------------------------------------------------------------------------------*/
	// forward all hit-related method calls to the grids (dayGrid might not be defined)


	hitsNeeded: function () {
		this.resourceGrid.hitsNeeded();
	},


	hitsNotNeeded: function () {
		this.resourceGrid.hitsNotNeeded();
	},


	prepareHits: function () {
		this.resourceGrid.prepareHits();
	},


	releaseHits: function () {
		this.resourceGrid.releaseHits();
	},


	queryHit: function (left, top) {
		var hit = this.resourceGrid.queryHit(left, top);

		return hit;
	},


	getHitSpan: function (hit) {
		// TODO: hit.component is set as a hack to identify where the hit came from
		return hit.component.getHitSpan(hit);
	},


	getHitEl: function (hit) {
		// TODO: hit.component is set as a hack to identify where the hit came from
		return hit.component.getHitEl(hit);
	},


	/* Events
	------------------------------------------------------------------------------------------------------------------*/


	// Renders events onto the view and populates the View's segment array
	renderEvents: function (events) {
		var dayEvents = [];
		var timedEvents = [];
		var daySegs = [];
		var timedSegs;
		var i;

		// separate the events into all-day and timed
		for (i = 0; i < events.length; i++) {
			if (events[i].allDay) {
				dayEvents.push(events[i]);
			}
			else {
				timedEvents.push(events[i]);
			}
		}

		// render the events in the subcomponents
		timedSegs = this.resourceGrid.renderEvents(timedEvents);
		if (this.dayGrid) {
			daySegs = this.dayGrid.renderEvents(dayEvents);
		}

		// the all-day area is flexible and might have a lot of events, so shift the height
		this.updateHeight();
	},


	// Retrieves all segment objects that are rendered in the view
	getEventSegs: function () {
		return this.resourceGrid.getEventSegs();
	},


	// Unrenders all event elements and clears internal segment data
	unrenderEvents: function () {

		// unrender the events in the subcomponents
		this.resourceGrid.unrenderEvents();

		// we DON'T need to call updateHeight() because
		// a renderEvents() call always happens after this, which will eventually call updateHeight()
	},


	/* Dragging (for events and external elements)
	------------------------------------------------------------------------------------------------------------------*/


	// A returned value of `true` signals that a mock "helper" event has been rendered.
	renderDrag: function (dropLocation, seg) {
		if (dropLocation.start.hasTime()) {
			return this.resourceGrid.renderDrag(dropLocation, seg);
		}
	},


	unrenderDrag: function () {
		this.resourceGrid.unrenderDrag();
	},


	/* Selection
	------------------------------------------------------------------------------------------------------------------*/


	// Renders a visual indication of a selection
	renderSelection: function (span) {
		if (span.start.hasTime() || span.end.hasTime()) {
			this.resourceGrid.renderSelection(span);
		}
	},


	// Unrenders a visual indications of a selection
	unrenderSelection: function () {
		this.resourceGrid.unrenderSelection();
	},

	/* Day Click
	------------------------------------------------------------------------------------------------------------------*/


	// Triggers handlers to 'dayClick'
	// Span has start/end of the clicked area. Only the start is useful.
	triggerDayClick: function (span, dayEl, ev) {
		if (dayEl.length > 0) {
			for (var i = 0, atts = dayEl[0].attributes; i < atts.length; i++) {
				if (atts[i].nodeName === 'data-resource') {
					ev.resource = atts[i].nodeValue;
				}
			}
		}
		this.publiclyTrigger(
			'dayClick',
			dayEl,
			this.calendar.applyTimezone(span.start), // convert to calendar's timezone for external API
			ev
		);
	},

	triggerRightClick: function (span, dayEl, ev) {
		// is always triggered twice, we only want the second event
		if(lastRightClickEvent != null) {
			if (dayEl.length > 0) {
				for (var i = 0, atts = dayEl[0].attributes; i < atts.length; i++) {
					if (atts[i].nodeName === 'data-resource') {
						ev.resource = atts[i].nodeValue;
					}
				}
			}
			this.publiclyTrigger(
				'rightClick',
				dayEl,
				this.calendar.applyTimezone(span.start), // convert to calendar's timezone for external API
				ev
			);
			lastRightClickEvent = null;	
		} else {
			lastRightClickEvent = ev;
		}
	}
});

FC.views.agendaParallel = ParallelView; // register our class with the view system