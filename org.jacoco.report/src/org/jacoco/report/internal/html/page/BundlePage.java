/*******************************************************************************
 * Copyright (c) 2009, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.report.internal.html.page;

import java.io.IOException;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.IHTMLReportContext;
import org.jacoco.report.internal.html.PackagePage;

/**
 * Page showing coverage information for a bundle. The page contains a table
 * with all packages of the bundle.
 */
public class BundlePage extends TablePage<IBundleCoverage> {

	private final ISourceFileLocator locator;

	/**
	 * Creates a new visitor in the given context.
	 * 
	 * @param node
	 * @param parent
	 * @param locator
	 * @param folder
	 * @param context
	 */
	public BundlePage(final IBundleCoverage node, final ReportPage parent,
			final ISourceFileLocator locator, final ReportOutputFolder folder,
			final IHTMLReportContext context) {
		super(node, parent, folder, context);
		this.locator = locator;
	}

	@Override
	public void render() throws IOException {
		renderPackages();
		super.render();
	}

	private void renderPackages() throws IOException {
		for (final IPackageCoverage p : getNode().getPackages()) {
			final String packagename = p.getName();
			final String foldername = packagename.length() == 0 ? "default"
					: packagename.replace('/', '.');
			final PackagePage page = new PackagePage(p, this, locator,
					folder.subFolder(foldername), context);
			page.render();
			addItem(page);
		}
	}

	@Override
	protected String getOnload() {
		return "initialSort(['breadcrumb', 'coveragetable'])";
	}

	@Override
	protected String getFileName() {
		return "index.html";
	}

}