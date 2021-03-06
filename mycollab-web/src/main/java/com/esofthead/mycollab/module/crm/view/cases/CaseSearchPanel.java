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
package com.esofthead.mycollab.module.crm.view.cases;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.core.arguments.*;
import com.esofthead.mycollab.core.db.query.Param;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.criteria.CaseSearchCriteria;
import com.esofthead.mycollab.module.crm.events.CaseEvent;
import com.esofthead.mycollab.module.crm.i18n.CaseI18nEnum;
import com.esofthead.mycollab.module.crm.ui.components.CrmViewHeader;
import com.esofthead.mycollab.module.crm.view.account.AccountSelectionField;
import com.esofthead.mycollab.module.user.ui.components.ActiveUserListSelect;
import com.esofthead.mycollab.security.RolePermissionCollections;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.DefaultGenericSearchPanel;
import com.esofthead.mycollab.vaadin.ui.DynamicQueryParamLayout;
import com.esofthead.mycollab.vaadin.ui.HeaderWithFontAwesome;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.maddon.layouts.MHorizontalLayout;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 */
public class CaseSearchPanel extends DefaultGenericSearchPanel<CaseSearchCriteria> {
	private static final long serialVersionUID = 1L;

	private static Param[] paramFields = new Param[] {
			CaseSearchCriteria.p_account, CaseSearchCriteria.p_priority,
			CaseSearchCriteria.p_status, CaseSearchCriteria.p_email,
			CaseSearchCriteria.p_origin, CaseSearchCriteria.p_reason,
			CaseSearchCriteria.p_subject, CaseSearchCriteria.p_type,
			CaseSearchCriteria.p_createdtime,
			CaseSearchCriteria.p_lastupdatedtime };

    @Override
    protected HeaderWithFontAwesome buildSearchTitle() {
        return new CrmViewHeader(CrmTypeConstants.CASE,
                AppContext.getMessage(CaseI18nEnum.VIEW_LIST_TITLE));
    }

    @Override
    protected void buildExtraControls() {
        final Button createCaseBtn = new Button(
                AppContext.getMessage(CaseI18nEnum.BUTTON_NEW_CASE),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        EventBusFactory.getInstance().post(
                                new CaseEvent.GotoAdd(this, null));
                    }
                });
        createCaseBtn.setIcon(FontAwesome.PLUS);
        createCaseBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
        createCaseBtn.setEnabled(AppContext
                .canWrite(RolePermissionCollections.CRM_CASE));
        this.addHeaderRight(createCaseBtn);
    }

    @SuppressWarnings("unchecked")
	@Override
	protected BasicSearchLayout<CaseSearchCriteria> createBasicSearchLayout() {
		return new CaseBasicSearchLayout();
	}

	@Override
	protected SearchLayout<CaseSearchCriteria> createAdvancedSearchLayout() {
		return new CaseAdvancedSearchLayout();
	}

	private class CaseAdvancedSearchLayout extends
			DynamicQueryParamLayout<CaseSearchCriteria> {
		private static final long serialVersionUID = 1L;

		public CaseAdvancedSearchLayout() {
			super(CaseSearchPanel.this, CrmTypeConstants.CASE);
		}

		@Override
		public ComponentContainer constructHeader() {
			return CaseSearchPanel.this.constructHeader();
		}

		@Override
		public Param[] getParamFields() {
			return paramFields;
		}

		@Override
		protected Class<CaseSearchCriteria> getType() {
			return CaseSearchCriteria.class;
		}

		@Override
		protected Component buildSelectionComp(String fieldId) {
			if ("case-assignuser".equals(fieldId)) {
				return new ActiveUserListSelect();
			} else if ("case-account".equals(fieldId)) {
				return new AccountSelectionField();
			}
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private class CaseBasicSearchLayout extends BasicSearchLayout {

		private static final long serialVersionUID = 1L;
		private TextField subjectField;
		private CheckBox myItemCheckbox;

		@SuppressWarnings("unchecked")
		public CaseBasicSearchLayout() {
			super(CaseSearchPanel.this);
		}

		@Override
		public ComponentContainer constructHeader() {
			return CaseSearchPanel.this.constructHeader();
		}

		@SuppressWarnings("serial")
		@Override
		public ComponentContainer constructBody() {
			final MHorizontalLayout basicSearchBody = new MHorizontalLayout().withMargin(true);

			this.subjectField = this.createSeachSupportTextField(
					new TextField(), "subjectFieldName");
			this.subjectField.setWidth(UIConstants.DEFAULT_CONTROL_WIDTH);
			basicSearchBody.with(subjectField).withAlign(subjectField,
					Alignment.MIDDLE_CENTER);

			this.myItemCheckbox = new CheckBox(
					AppContext
							.getMessage(GenericI18Enum.SEARCH_MYITEMS_CHECKBOX));
			this.myItemCheckbox.setWidth("75px");
			basicSearchBody.with(myItemCheckbox).withAlign(myItemCheckbox,
					Alignment.MIDDLE_CENTER);

			final Button searchBtn = new Button(
					AppContext.getMessage(GenericI18Enum.BUTTON_SEARCH));
			searchBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
			searchBtn.setIcon(FontAwesome.SEARCH);

			searchBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					CaseBasicSearchLayout.this.callSearchAction();
				}
			});

			basicSearchBody.with(searchBtn).withAlign(searchBtn,
					Alignment.MIDDLE_LEFT);

			final Button cancelBtn = new Button(
					AppContext.getMessage(GenericI18Enum.BUTTON_CLEAR));
			cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);
			cancelBtn.addStyleName("cancel-button");
			cancelBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(final ClickEvent event) {
					CaseBasicSearchLayout.this.subjectField.setValue("");
				}
			});
			basicSearchBody.with(cancelBtn).withAlign(cancelBtn,
					Alignment.MIDDLE_CENTER);

			final Button advancedSearchBtn = new Button(
					AppContext
							.getMessage(GenericI18Enum.BUTTON_ADVANCED_SEARCH),
					new Button.ClickListener() {
						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(final ClickEvent event) {
							CaseSearchPanel.this.moveToAdvancedSearchLayout();
						}
					});
			advancedSearchBtn.setStyleName("link");
			basicSearchBody.with(advancedSearchBtn).withAlign(
					advancedSearchBtn, Alignment.MIDDLE_CENTER);
			return basicSearchBody;
		}

		@Override
		protected SearchCriteria fillUpSearchCriteria() {
            CaseSearchCriteria searchCriteria = new CaseSearchCriteria();
			searchCriteria
					.setSaccountid(new NumberSearchField(SearchField.AND,
							AppContext.getAccountId()));

			if (StringUtils.isNotBlank(this.subjectField.getValue().trim())) {
				searchCriteria
						.setSubject(new StringSearchField(SearchField.AND,
								this.subjectField.getValue().trim()));
			}

			if (this.myItemCheckbox.getValue()) {
				searchCriteria
						.setAssignUsers(new SetSearchField<>(
								SearchField.AND, new String[] { AppContext
										.getUsername() }));
			} else {
				searchCriteria.setAssignUsers(null);
			}
			return searchCriteria;
		}
	}
}
