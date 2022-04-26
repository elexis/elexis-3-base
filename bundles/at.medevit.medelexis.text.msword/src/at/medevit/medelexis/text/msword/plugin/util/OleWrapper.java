/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;

/**
 * Abstract implementation of stuff all OLE/COM wrapper classes have in common.
 * OLE/COM invocation is encapsulated into Runnable. Currently we assume running
 * in display thread.
 *
 * @author thomashu
 *
 */
public abstract class OleWrapper {
	protected Display display;

	protected OleAutomation oleObj;

	public OleWrapper(OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		this.display = display;
		oleObj = oleAuto;
		manager.add(this);

		GlobalOleWordWrapperManager.add(oleObj);
	}

	protected synchronized void run(OleRunnable runnable) {
		try {
			Thread.yield();
			runnable.run();
			checkOleException();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			if (ex instanceof RuntimeException)
				throw ((RuntimeException) ex);
		}
	}

	protected void checkOleException() {
		String lastError = oleObj.getLastError();
		if (lastError != null && !lastError.startsWith("No Error")) //$NON-NLS-1$
			throw new IllegalStateException("OleWrapper error: " + oleObj.getLastError()); //$NON-NLS-1$
	}

	protected abstract int getIdForMember(String member);

	protected Variant runInvoke(final String method) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				returnVariant = OleUtil.invoke(oleObj, getIdForMember(method));
			}
		};
		run(runnable);
		return runnable.returnVariant;
	}

	protected Variant runInvoke(final String method, final Variant[] arguments) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				returnVariant = OleUtil.invoke(oleObj, getIdForMember(method), arguments);
			}
		};
		run(runnable);
		return runnable.returnVariant;
	}

	protected Variant runInvoke(final String method, final Variant[] arguments, final String[] argumentsNames) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				// add the method name to the argumentsNames as expected by getIDsOfNames
				String[] idArgumentsNames = new String[argumentsNames.length + 1];
				idArgumentsNames[0] = method;
				for (int i = 1; i < idArgumentsNames.length; i++) {
					idArgumentsNames[i] = argumentsNames[i - 1];
				}
				int[] ids = oleObj.getIDsOfNames(idArgumentsNames);
				// remove the id of the method from the argumentsIds array
				int[] argumentsIds = new int[ids.length - 1];
				for (int i = 0; i < argumentsIds.length; i++) {
					argumentsIds[i] = ids[i + 1];
				}

				returnVariant = OleUtil.invoke(oleObj, getIdForMember(method), arguments, argumentsIds);

			}
		};
		run(runnable);
		return runnable.returnVariant;
	}

	protected void runSetProperty(final String property, final Variant value) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				OleUtil.setProperty(oleObj, getIdForMember(property), value);
			}
		};
		run(runnable);
	}

	protected OleAutomation runGetOleAutomationProperty(final String property) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				returnAutomation = OleUtil.getOleAutomationProperty(oleObj, getIdForMember(property));
			}
		};
		run(runnable);
		return runnable.returnAutomation;
	}

	protected Variant runGetVariantProperty(final String property) {
		OleRunnable runnable = new OleRunnable() {
			@Override
			public void run() {
				returnVariant = OleUtil.getVariantProperty(oleObj, getIdForMember(property));
			}
		};
		run(runnable);
		return runnable.returnVariant;
	}

	public OleAutomation getOleObj() {
		return oleObj;
	}
}
