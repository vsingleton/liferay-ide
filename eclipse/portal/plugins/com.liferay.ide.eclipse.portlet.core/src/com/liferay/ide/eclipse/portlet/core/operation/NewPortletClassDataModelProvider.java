/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.operation;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.server.core.IPortalRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( {
	"restriction", "unchecked", "rawtypes"
})
public class NewPortletClassDataModelProvider extends NewWebClassDataModelProvider
	implements INewPortletClassDataModelProperties, IPluginWizardFragmentProperties {

	protected Properties categories;

	protected TemplateContextType contextType;

	protected TemplateStore templateStore;

	protected boolean fragment;

	public NewPortletClassDataModelProvider(
		TemplateStore templateStore, TemplateContextType contextType, boolean fragment) {
		super();

		this.templateStore = templateStore;
		this.contextType = contextType;
		this.fragment = fragment;
	}

	public NewPortletClassDataModelProvider() {
		super();
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new AddPortletOperation(this.model);
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SUPERCLASS.equals(propertyName)) {
			return QUALIFIED_MVC_PORTLET;
		}
		else if (CONSTRUCTOR.equals(propertyName)) {
			return false;
		}
		else if (INIT_OVERRIDE.equals(propertyName) || DOVIEW_OVERRIDE.equals(propertyName)) {
			return true;
		}
		else if (DESTROY_OVERRIDE.equals(propertyName)) {
			return false;
		}
		else if (JAVA_PACKAGE.equals(propertyName)) {
			return "com.test";
		}
		else if (CLASS_NAME.equals(propertyName)) {
			return "NewPortlet";
		}
		else if (PORTLET_NAME.equals(propertyName) || LIFERAY_PORTLET_NAME.equals(propertyName)) {
			return getProperty(CLASS_NAME).toString().toLowerCase();
		}
		else if (DISPLAY_NAME.equals(propertyName) || TITLE.equals(propertyName) || SHORT_TITLE.equals(propertyName)) {
			return getProperty(CLASS_NAME);
		}
		else if (KEYWORDS.equals(propertyName)) {
			return "";
		}
		else if (INIT_PARAMS.equals(propertyName)) {
			return getInitParams();
		}
		else if (VIEW_MODE.equals(propertyName)) {
			return true;
		}
		else if (CREATE_JSPS.equals(propertyName)) {
			return true;
		}
		else if (CREATE_JSPS_FOLDER.equals(propertyName)) {
			return "/html/" + getProperty(CLASS_NAME).toString().toLowerCase();
		}
		else if (ICON_FILE.equals(propertyName)) {
			return "/icon.png";
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE.equals(propertyName)) {
			return false;
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE_PATH.equals(propertyName)) {
			return "content/Language.properties";
		}
		else if (ALLOW_MULTIPLE.equals(propertyName)) {
			return false;
		}
		else if (CSS_FILE.equals(propertyName)) {
			return "/css/portlet.css";
		}
		else if (JAVASCRIPT_FILE.equals(propertyName)) {
			return "/js/javascript.js";
		}
		else if (CSS_CLASS_WRAPPER.equals(propertyName)) {
			return getProperty(CLASS_NAME).toString().toLowerCase() + ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (ID.equals(propertyName)) {
			return getProperty(PORTLET_NAME);
		}
		else if (CATEGORY.equals(propertyName)) {
			return "category.sample";
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (VIEW_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "VIEW");
		}
		else if (EDIT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "EDIT");
		}
		else if (HELP_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "HELP");
		}
		/**
		 * Values for liferay modes taking from LiferayPortletMode.java
		 */
		else if (ABOUT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "about");
		}
		else if (CONFIG_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "config");
		}
		else if (EDITDEFAULTS_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "edit_defaults");
		}
		else if (EDITGUEST_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "edit_guest");
		}
		else if (PREVIEW_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "preview");
		}
		else if (PRINT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "print");
		}
		else if (CATEGORY.equals(propertyName)) {
			if (getProperty(CATEGORY).equals("category.sample")) {
				return new DataModelPropertyDescriptor("category.sample", "Sample");
			}
		}

		return super.getPropertyDescriptor(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		// propertyNames.add(CREATE_CUSTOM_PORTLET_CLASS);
		// propertyNames.add(PORTLET_CLASS);

		propertyNames.add(TEMPLATE_STORE);
		propertyNames.add(CONTEXT_TYPE);

		propertyNames.add(INIT_OVERRIDE);
		propertyNames.add(DESTROY_OVERRIDE);
		propertyNames.add(DOVIEW_OVERRIDE);
		propertyNames.add(DOEDIT_OVERRIDE);
		propertyNames.add(DOHELP_OVERRIDE);
		propertyNames.add(DOABOUT_OVERRIDE);
		propertyNames.add(DOCONFIG_OVERRIDE);
		propertyNames.add(DOEDITDEFAULTS_OVERRIDE);
		propertyNames.add(DOEDITGUEST_OVERRIDE);
		propertyNames.add(DOPREVIEW_OVERRIDE);
		propertyNames.add(DOPRINT_OVERRIDE);
		propertyNames.add(PROCESSACTION_OVERRIDE);

		propertyNames.add(PORTLET_NAME);
		propertyNames.add(DISPLAY_NAME);
		propertyNames.add(TITLE);
		propertyNames.add(SHORT_TITLE);
		propertyNames.add(KEYWORDS);
		propertyNames.add(INIT_PARAMS);

		propertyNames.add(VIEW_MODE);
		propertyNames.add(EDIT_MODE);
		propertyNames.add(HELP_MODE);

		propertyNames.add(ABOUT_MODE);
		propertyNames.add(CONFIG_MODE);
		propertyNames.add(EDITDEFAULTS_MODE);
		propertyNames.add(EDITGUEST_MODE);
		propertyNames.add(PREVIEW_MODE);
		propertyNames.add(PRINT_MODE);

		propertyNames.add(CREATE_RESOURCE_BUNDLE_FILE);
		propertyNames.add(CREATE_RESOURCE_BUNDLE_FILE_PATH);
		propertyNames.add(CREATE_JSPS);
		propertyNames.add(CREATE_JSPS_FOLDER);

		propertyNames.add(LIFERAY_PORTLET_NAME);
		propertyNames.add(ICON_FILE);
		propertyNames.add(ALLOW_MULTIPLE);
		propertyNames.add(CSS_FILE);
		propertyNames.add(JAVASCRIPT_FILE);
		propertyNames.add(CSS_CLASS_WRAPPER);
		propertyNames.add(ID);
		propertyNames.add(CATEGORY);

		propertyNames.add(FACET_RUNTIME);
		propertyNames.add(REMOVE_EXISTING_ARTIFACTS);

		return propertyNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (SUPERCLASS.equals(propertyName)) {
			String[] vals = new String[] {
				QUALIFIED_MVC_PORTLET, QUALIFIED_LIFERAY_PORTLET, QUALIFIED_GENERIC_PORTLET
			};

			return DataModelPropertyDescriptor.createDescriptors(vals, vals);
		}
		else if (CATEGORY.equals(propertyName)) {
			Properties categories = getCategories();

			if (categories != null && categories.size() > 0) {
				return DataModelPropertyDescriptor.createDescriptors(
					categories.keySet().toArray(new Object[0]), categories.values().toArray(new String[0]));
			}
		}

		return super.getValidPropertyDescriptors(propertyName);
	}

	@Override
	public void init() {
		super.init();

		this.setProperty(TEMPLATE_STORE, this.templateStore);
		this.setProperty(CONTEXT_TYPE, this.contextType);
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (VIEW_MODE.equals(propertyName)) {
			return false;
		}
		else if (INIT_OVERRIDE.equals(propertyName)) {
			return false;
		}
		else if (ABOUT_MODE.equals(propertyName) || CONFIG_MODE.equals(propertyName) ||
			EDITDEFAULTS_MODE.equals(propertyName) || EDITGUEST_MODE.equals(propertyName) ||
			PREVIEW_MODE.equals(propertyName) || PRINT_MODE.equals(propertyName)) {

			if (this.fragment) {
				return true;
			}

			return PortletSupertypesValidator.isLiferayPortletSuperclass(getDataModel(), true);
		}
		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (VIEW_MODE.equals(propertyName)) {
			setProperty(DOVIEW_OVERRIDE, propertyValue);
		}
		else if (EDIT_MODE.equals(propertyName)) {
			setProperty(DOEDIT_OVERRIDE, propertyValue);
		}
		else if (HELP_MODE.equals(propertyName)) {
			setProperty(DOHELP_OVERRIDE, propertyValue);
		}
		else if (ABOUT_MODE.equals(propertyName) &&
			PortletSupertypesValidator.isLiferayPortletSuperclass(getDataModel())) {

			setProperty(DOABOUT_OVERRIDE, propertyValue);
		}
		else if (CONFIG_MODE.equals(propertyName)) {
			setProperty(DOCONFIG_OVERRIDE, propertyValue);
		}
		else if (EDITDEFAULTS_MODE.equals(propertyName)) {
			setProperty(DOEDITDEFAULTS_OVERRIDE, propertyValue);
		}
		else if (EDITGUEST_MODE.equals(propertyName)) {
			setProperty(DOEDITGUEST_OVERRIDE, propertyValue);
		}
		else if (PREVIEW_MODE.equals(propertyName)) {
			setProperty(DOPREVIEW_OVERRIDE, propertyValue);
		}
		else if (PRINT_MODE.equals(propertyName)) {
			setProperty(DOPRINT_OVERRIDE, propertyValue);
		}
		else if (SUPERCLASS.equals(propertyName)) {
			getDataModel().notifyPropertyChange(VIEW_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDIT_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(HELP_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(ABOUT_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(CONFIG_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDITDEFAULTS_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDITGUEST_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(PREVIEW_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(PRINT_MODE, IDataModel.ENABLE_CHG);
		}
		else if (PORTLET_NAME.equals(propertyName)) {
			String portletName = getStringProperty(propertyName);

			getDataModel().setStringProperty(LIFERAY_PORTLET_NAME, portletName);
		}
		else if (LIFERAY_PORTLET_NAME.equals(propertyName)) {
			String liferayPortletName = getStringProperty(propertyName);

			getDataModel().setStringProperty(PORTLET_NAME, liferayPortletName);
		}

		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (PORTLET_NAME.equals(propertyName)) {
			// must have a valid portlet name
			String portletName = getStringProperty(PORTLET_NAME);

			if (portletName == null || portletName.length() == 0) {
				return PortletCore.createErrorStatus("Portlet name is empty.");
			}
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE_PATH.equals(propertyName)) {
			boolean validPath = false;
			boolean validFileName = false;

			String val = getStringProperty(propertyName);

			if (getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE) && CoreUtil.isNullOrEmpty(val)) {
				return PortletCore.createErrorStatus("Resource bundle file path must be a valid path.");
			}

			try {
				IPath path = new Path(val);
				validPath = path.isValidPath(val);

				if ("properties".equals(path.getFileExtension())) {
					validFileName = true;
				}
			}
			catch (Exception e) {
				// eat errors
			}

			if (!validPath) {
				return PortletCore.createErrorStatus("Resource bundle file path must be a valid path.");
			}

			if (validFileName) {
				return super.validate(propertyName);
			}
			else {
				return PortletCore.createWarningStatus("Resource bundle file path should end with .properties");
			}
		}
		else if (CREATE_JSPS_FOLDER.equals(propertyName)) {
			String folderValue = getStringProperty(propertyName);

			IProject targetProject = getTargetProject();

			if ((!CoreUtil.isNullOrEmpty(folderValue)) && targetProject != null) {
				IFolder docroot = ProjectUtil.getDocroot(targetProject);
				String errorMsg = FileUtil.validateNewFolder(docroot, folderValue);

				if (errorMsg != null) {
					return PortletCore.createErrorStatus(errorMsg);
				}

				// make sure path first segment isn't the same as the portlet name
				String path = new Path(folderValue).segment(0);
				if (!CoreUtil.isNullOrEmpty(path) && path.equals(getStringProperty(PORTLET_NAME))) {
					return PortletCore.createErrorStatus("JSP folder cannot match portlet name");
				}
			}
		}
		else if ((SUPERCLASS.equals(propertyName) || SOURCE_FOLDER.equals(propertyName)) && this.fragment) {
			return Status.OK_STATUS;
		}

		return super.validate(propertyName);
	}

	protected ParamValue[] createDefaultParamValuesForModes(String[] modes, String[] names, String[] values) {
		Assert.isTrue(modes != null && names != null && values != null && (modes.length == names.length) &&
			(names.length == values.length));

		List<ParamValue> defaultParams = new ArrayList<ParamValue>();

		// for each path value need to prepend a path that will be specific to
		// the portlet being created
		String prependPath = getDataModel().getStringProperty(CREATE_JSPS_FOLDER);

		for (int i = 0; i < modes.length; i++) {
			if (getBooleanProperty(modes[i])) {
				ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();

				paramValue.setName(names[i]);

				if (CoreUtil.isNullOrEmpty(prependPath)) {
					paramValue.setValue(values[i]);
				}
				else {
					if (CoreUtil.isNullOrEmpty(prependPath) || (!prependPath.startsWith("/"))) {
						prependPath = "/" + prependPath;
					}
					paramValue.setValue(prependPath + values[i]);
				}

				defaultParams.add(paramValue);
			}
		}

		return defaultParams.toArray(new ParamValue[0]);
	}

	protected Properties getCategories() {
		if (categories == null) {
			IProject project = (IProject) getProperty(PROJECT);

			if (project != null) {
				try {
					IRuntime runtime = null;

					if (this.fragment) {
						org.eclipse.wst.common.project.facet.core.runtime.IRuntime bRuntime =
							(org.eclipse.wst.common.project.facet.core.runtime.IRuntime) getDataModel().getProperty(
								FACET_RUNTIME);
						runtime = ServerUtil.getRuntime(bRuntime);
					} 
					else {
						runtime = ServerUtil.getRuntime(project);
					}

					IPortalRuntime portalRuntime = (IPortalRuntime) runtime.createWorkingCopy().loadAdapter(
							IPortalRuntime.class, null);

					categories = portalRuntime.getCategories();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return categories;
	}

	protected Object getInitParams() {
		List<ParamValue> initParams = new ArrayList<ParamValue>();

		// if the user is using MVCPortlet and creating JSPs then we need to
		// define init-params for each view mode that is checked
		if (/* getStringProperty(SUPERCLASS).equals(QUALIFIED_MVC_PORTLET) && */getBooleanProperty(CREATE_JSPS)) {
			String[] modes = ALL_PORTLET_MODES;

			String[] names =
				{
					"view-jsp", "edit-jsp", "help-jsp", "about-jsp", "config-jsp", "edit-defaults-jsp",
					"edit-guest-jsp", "preview-jsp", "print-jsp"
				};

			String[] values =
				{
					"/view.jsp", "/edit.jsp", "/help.jsp", "/about.jsp", "/config.jsp", "/edit-defaults.jsp",
					"/edit-guest.jsp", "/preview.jsp", "/print.jsp"
				};
			ParamValue[] paramVals = createDefaultParamValuesForModes(modes, names, values);

			Collections.addAll(initParams, paramVals);
		}

		return initParams;
	}

	protected IFile getWorkspaceFile(IPath file) {
		IFile retval = null;

		try {
			retval = ResourcesPlugin.getWorkspace().getRoot().getFile(file);
		}
		catch (Exception e) {
			// best effort
		}

		return retval;
	}

	@Override
	protected IStatus validateJavaClassName(String className) {
		return super.validateJavaClassName(className);
	}
}
