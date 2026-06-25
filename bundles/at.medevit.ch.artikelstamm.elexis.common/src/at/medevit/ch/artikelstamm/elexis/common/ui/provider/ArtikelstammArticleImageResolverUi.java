/*******************************************************************************
 * Copyright (c) 2026 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package at.medevit.ch.artikelstamm.elexis.common.ui.provider;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.ui.medication.IArticleImageResolverUi;

/**
 * Provides the article marking {@link Image} (P/SL/nonPharma/blackbox) of an
 * {@link IArtikelstammItem} to the medication list, so that the markings shown
 * in the article list (Artikelstamm) also appear in the medication list. Reuses
 * the icon logic of {@link LagerhaltungArtikelstammLabelProvider}.
 *
 * <p>
 * The returned icons are rendered with reduced opacity ({@link #OPACITY}) so
 * they do not visually overpower the entry type symbols of the medication list.
 * </p>
 */
@Component
public class ArtikelstammArticleImageResolverUi implements IArticleImageResolverUi {

	/** Opacity factor applied to the article marking icons (0.0 - 1.0). */
	private static final double OPACITY = 0.65;

	private final LagerhaltungArtikelstammLabelProvider labelProvider = new LagerhaltungArtikelstammLabelProvider();

	/** Cache of faded icons, keyed by the (shared, static) source image. */
	private final Map<Image, Image> fadedCache = Collections.synchronizedMap(new IdentityHashMap<>());

	@Override
	public Image getImage(IArticle article) {
		if (article instanceof IArtikelstammItem) {
			Image source = labelProvider.getImage(article);
			if (source == null) {
				return null;
			}
			return getFaded(source);
		}
		return null;
	}

	/**
	 * Return a cached, reduced-opacity copy of the given source image. The source
	 * image is never modified.
	 */
	private Image getFaded(Image source) {
		Image faded = fadedCache.get(source);
		if (faded == null || faded.isDisposed()) {
			faded = createFaded(source);
			fadedCache.put(source, faded);
		}
		return faded;
	}

	private Image createFaded(Image source) {
		ImageData data = source.getImageData();
		if (data.alphaData != null) {
			// per-pixel alpha (e.g. PNG): scale every pixel's alpha
			byte[] alpha = data.alphaData;
			for (int i = 0; i < alpha.length; i++) {
				alpha[i] = (byte) Math.round((alpha[i] & 0xFF) * OPACITY);
			}
			data.alphaData = alpha;
		} else if (data.alpha != -1) {
			// uniform alpha already set
			data.alpha = (int) Math.round(data.alpha * OPACITY);
		} else {
			// otherwise apply a global alpha to the whole image
			data.alpha = (int) Math.round(255 * OPACITY);
		}
		return new Image(Display.getDefault(), data);
	}
}
