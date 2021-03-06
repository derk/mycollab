/**
 * This file is part of mycollab-services.
 *
 * mycollab-services is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-services is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-services.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.project.service.ibatis;

import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esofthead.mycollab.common.ActivityStreamConstants;
import com.esofthead.mycollab.common.ModuleNameConstants;
import com.esofthead.mycollab.common.domain.ActivityStream;
import com.esofthead.mycollab.common.service.ActivityStreamService;
import com.esofthead.mycollab.module.page.domain.Page;
import com.esofthead.mycollab.module.page.service.PageService;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.service.ProjectPageService;

@Service
public class ProjectPageServiceImpl implements ProjectPageService {

	@Autowired
	private PageService pageService;

	@Autowired
	private ActivityStreamService activityStreamService;

	@Override
	public void savePage(Page page, String createdUser, Integer projectId,
			Integer accountId) {
		pageService.savePage(page, createdUser);

		ActivityStream activityStream = new ActivityStream();
		activityStream.setAction(ActivityStreamConstants.ACTION_CREATE);
		activityStream.setCreateduser(createdUser);
		activityStream.setCreatedtime(new GregorianCalendar().getTime());
		activityStream.setModule(ModuleNameConstants.PRJ);
		activityStream.setNamefield(page.getSubject());
		activityStream.setSaccountid(accountId);
		activityStream.setType(ProjectTypeConstants.PAGE);
		activityStream.setTypeid(page.getPath());
		activityStream.setExtratypeid(projectId);
		activityStreamService.save(activityStream);
	}

	@Override
	public Page getPage(String path, String requestedUser) {
		return pageService.getPage(path, requestedUser);
	}

}
