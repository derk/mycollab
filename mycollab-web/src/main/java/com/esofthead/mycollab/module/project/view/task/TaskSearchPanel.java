/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.view.task;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.core.arguments.NumberSearchField;
import com.esofthead.mycollab.core.arguments.SearchField;
import com.esofthead.mycollab.core.arguments.StringSearchField;
import com.esofthead.mycollab.core.db.query.Param;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.domain.criteria.TaskSearchCriteria;
import com.esofthead.mycollab.module.project.events.TaskListEvent;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.ui.components.ProjectViewHeader;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectMemberListSelect;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.DefaultGenericSearchPanel;
import com.esofthead.mycollab.vaadin.ui.DynamicQueryParamLayout;
import com.esofthead.mycollab.vaadin.ui.HeaderWithFontAwesome;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.maddon.layouts.MHorizontalLayout;

/**
 * 
 * @author MyCollab Ltd.
 * @since 4.0
 * 
 */
public class TaskSearchPanel extends DefaultGenericSearchPanel<TaskSearchCriteria> {
	private static final long serialVersionUID = 1L;
	protected TaskSearchCriteria searchCriteria;

	private static Param[] paramFields = new Param[] {
			TaskSearchCriteria.p_assignee, TaskSearchCriteria.p_createtime,
			TaskSearchCriteria.p_duedate, TaskSearchCriteria.p_lastupdatedtime,
			TaskSearchCriteria.p_status };

    @Override
    protected HeaderWithFontAwesome buildSearchTitle() {
        return new ProjectViewHeader(ProjectTypeConstants.TASK, "Tasks");
    }

    @Override
    protected void buildExtraControls() {
        Button backBtn = new Button(
                AppContext.getMessage(TaskI18nEnum.BUTTON_BACK_TO_DASHBOARD),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        EventBusFactory.getInstance()
                                .post(new TaskListEvent.GotoTaskListScreen(
                                        this, null));

                    }
                });
        backBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
        backBtn.setIcon(FontAwesome.ARROW_LEFT);
        addHeaderRight(backBtn);
    }

    @Override
	protected SearchLayout<TaskSearchCriteria> createBasicSearchLayout() {
		return new TaskBasicSearchLayout();
	}

	@Override
	protected SearchLayout<TaskSearchCriteria> createAdvancedSearchLayout() {
		return new TaskAdvancedSearchLayout();
	}

	public void setTextField(String name) {
		if (getCompositionRoot() instanceof TaskBasicSearchLayout) {
			((TaskBasicSearchLayout) getCompositionRoot()).setNameField(name);
		}
	}

	public void getAdvanceSearch() {
		moveToAdvancedSearchLayout();
	}

	public void getBasicSearch() {
		moveToBasicSearchLayout();
	}

	private class TaskBasicSearchLayout extends BasicSearchLayout<TaskSearchCriteria> {
		private static final long serialVersionUID = 1L;

		public TaskBasicSearchLayout() {
			super(TaskSearchPanel.this);
		}

		private TextField nameField;
		private CheckBox myItemCheckbox;

		public void setNameField(String value) {
			this.nameField.setValue(value);
		}

		@Override
		public ComponentContainer constructBody() {
			final MHorizontalLayout basicSearchBody = new MHorizontalLayout().withMargin(true);

			Label nameLbl = new Label("Name:");
			basicSearchBody.with(nameLbl).withAlign(nameLbl, Alignment.MIDDLE_LEFT);

			this.nameField = new TextField();
			this.nameField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
			basicSearchBody.with(nameField).withAlign(nameField, Alignment.MIDDLE_CENTER);

			this.myItemCheckbox = new CheckBox(AppContext
							.getMessage(GenericI18Enum.SEARCH_MYITEMS_CHECKBOX));
			this.myItemCheckbox.setWidth("75px");
			basicSearchBody.with(myItemCheckbox).withAlign(myItemCheckbox, Alignment.MIDDLE_CENTER);

			final Button searchBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
			searchBtn.setIcon(FontAwesome.SEARCH);
			searchBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
			searchBtn.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(final ClickEvent event) {
					callSearchAction();
				}
			});
			basicSearchBody.with(searchBtn).withAlign(searchBtn, Alignment.MIDDLE_LEFT);

			final Button cancelBtn = new Button(AppContext.getMessage(GenericI18Enum.BUTTON_CLEAR));
			cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
			cancelBtn.addStyleName("cancel-button");
			cancelBtn.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(final ClickEvent event) {
					nameField.setValue("");
				}
			});
			basicSearchBody.with(cancelBtn).withAlign(cancelBtn, Alignment.MIDDLE_CENTER);

			final Button advancedSearchBtn = new Button(AppContext
							.getMessage(GenericI18Enum.BUTTON_ADVANCED_SEARCH),
					new Button.ClickListener() {
						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(final ClickEvent event) {
							moveToAdvancedSearchLayout();
						}
					});
			advancedSearchBtn.setStyleName("link");

			basicSearchBody.with(advancedSearchBtn).withAlign(
					advancedSearchBtn, Alignment.MIDDLE_CENTER);
			return basicSearchBody;
		}

		@Override
		protected TaskSearchCriteria fillUpSearchCriteria() {
			searchCriteria = new TaskSearchCriteria();
			searchCriteria.setProjectid(new NumberSearchField(
					CurrentProjectVariables.getProjectId()));
			searchCriteria.setTaskName(new StringSearchField(this.nameField.getValue().trim()));
			if (this.myItemCheckbox.getValue()) {
				searchCriteria.setAssignUser(new StringSearchField(
						SearchField.AND, AppContext.getUsername()));
			} else {
				searchCriteria.setAssignUser(null);
			}
			return searchCriteria;
		}

		@Override
		public ComponentContainer constructHeader() {
			return TaskSearchPanel.this.constructHeader();
		}

	}

	private class TaskAdvancedSearchLayout extends DynamicQueryParamLayout<TaskSearchCriteria> {
		private static final long serialVersionUID = 1L;

		public TaskAdvancedSearchLayout() {
			super(TaskSearchPanel.this, ProjectTypeConstants.TASK);
		}

		@Override
		public ComponentContainer constructHeader() {
            return TaskSearchPanel.this.constructHeader();
		}

		@Override
		protected Class<TaskSearchCriteria> getType() {
			return TaskSearchCriteria.class;
		}

		@Override
		public Param[] getParamFields() {
			return paramFields;
		}

		@Override
		protected Component buildSelectionComp(String fieldId) {
			if ("task-assignuser".equals(fieldId)) {
				return new ProjectMemberListSelect(false);
			}
			return null;
		}

		@Override
		protected TaskSearchCriteria fillUpSearchCriteria() {
			searchCriteria = super.fillUpSearchCriteria();
			searchCriteria.setProjectid(new NumberSearchField(
					CurrentProjectVariables.getProjectId()));
			return searchCriteria;
		}
	}
}