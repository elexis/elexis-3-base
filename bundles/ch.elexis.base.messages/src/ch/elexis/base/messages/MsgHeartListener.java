/*******************************************************************************
 * Copyright (c) 2007-2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.base.messages;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.model.IMessage;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;

public class MsgHeartListener implements HeartListener {
	static Logger log = LoggerFactory.getLogger(MsgHeartListener.class);
	boolean bSkip;

	@Override
	public void heartbeat() {
		if (!bSkip) {
			ContextServiceHolder.get().getActiveUserContact().ifPresent(uc -> {
				List<IMessage> res = CoreModelServiceHolder.get().getQuery(IMessage.class)
						.and("destination", COMPARATOR.EQUALS, uc.getId()).execute(); //$NON-NLS-1$
				if (!res.isEmpty()) {
					UiDesk.getDisplay().asyncExec(() -> {
						if (!isModalShellOpen()) {
							bSkip = true;
							if (ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_ON, true)) {
								playSound();
							}
							new MsgDetailDialog(Hub.getActiveShell(), res.get(0)).open();
							bSkip = false;
						}
					});
				}
			});
		}
	}

	private boolean isModalShellOpen() {
		return Arrays.asList(Display.getDefault().getShells()).stream()
				.filter(s -> ((s.getStyle() & SWT.SYSTEM_MODAL) > 0) || ((s.getStyle() & SWT.APPLICATION_MODAL) > 0))
				.findFirst().isPresent();
	}

	/**
	 * Plays a sound defined in the message preferences.
	 * <p>
	 * Supports default resources, local file paths, and URIs (e.g., file:/ or
	 * network paths).
	 * </p>
	 * Uses {@link DataLine.Info} to load the {@link Clip} to avoid
	 * IllegalArgumentExceptions caused by specific system audio configurations.
	 * * @see <a href=
	 * "http://stackoverflow.com/questions/26435282/issue-playing-audio-with-stackoverflows-javasound-tag-example">
	 * Stackoverflow Explanation</a>
	 */
	private void playSound() {
		try {
			AudioInputStream audioInStream;
			String soundPathValue = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_PATH,
					MessagePreferences.DEF_SOUND_PATH);

			if (MessagePreferences.DEF_SOUND_PATH.equals(soundPathValue)) {
				URL sound = getClass().getResource(soundPathValue);
				if (sound == null) {
					log.warn("Default sound resource not found: " + soundPathValue); //$NON-NLS-1$
					return;
				}
				audioInStream = AudioSystem.getAudioInputStream(sound);
			} else {
				if (soundPathValue.startsWith("file:") || soundPathValue.contains(":/")) { //$NON-NLS-1$ //$NON-NLS-2$
					java.net.URI uri = new java.net.URI(soundPathValue);
					audioInStream = AudioSystem.getAudioInputStream(uri.toURL());
				} else {
					File soundFile = new File(soundPathValue);
					if (!soundFile.exists()) {
						log.warn("Sound file [" + soundPathValue + "] not found"); //$NON-NLS-1$ //$NON-NLS-2$
						return;
					}
					audioInStream = AudioSystem.getAudioInputStream(soundFile);
				}
			}

			// load the sound into memory (a Clip)
			DataLine.Info info = new DataLine.Info(Clip.class, audioInStream.getFormat());
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioInStream);
			clip.start();

		} catch (Exception e) {
			log.error("Could not play message sound", e); //$NON-NLS-1$
			return;
		}
	}
}
