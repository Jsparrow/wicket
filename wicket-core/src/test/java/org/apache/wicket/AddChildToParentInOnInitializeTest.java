/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket;

import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Test;

/**
 * https://issues.apache.org/jira/browse/WICKET-6021
 */
class AddChildToParentInOnInitializeTest extends WicketTestCase
{
	@Test
    void addChildToParentInOnInitialize()
	{
		tester.startPage(TestPage.class);
		tester.assertRenderedPage(TestPage.class);
		tester.assertComponent(new StringBuilder().append(TestPage.PARENT_ID).append(':').append(TestPage.FIRST_CHILD_ID).toString(), WebMarkupContainer.class);
		tester.assertComponent(TestPage.PARENT_ID, Parent.class);
	}

	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		static final String FIRST_CHILD_ID = "firstChild";
		static final String PARENT_ID = "parentContainer";
		static final String SECOND_CHILD_ID = "thirdChild";
		static final String THIRD_CHILD_ID = "fourthChild";

		@Override
		protected void onInitialize()
		{
			super.onInitialize();

			final Parent parent = new Parent();
			add(parent);

			parent.addOrReplace(new WebMarkupContainer(TestPage.THIRD_CHILD_ID));
		}

		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass)
		{
			return new StringResourceStream(new StringBuilder().append("<html><head></head><body>").append("<div wicket:id='").append(PARENT_ID).append("'>").append("<div wicket:id='").append(FIRST_CHILD_ID).append("'></div>")
					.append("<div wicket:id='").append(SECOND_CHILD_ID).append("'></div>").append("<div wicket:id='").append(THIRD_CHILD_ID).append("'></div>").append("</div>").append("</body></html>")
					.toString());
		}
	}

	private static class Parent extends WebMarkupContainer
	{
		Parent()
		{
			super(TestPage.PARENT_ID);

			add(new WebMarkupContainer(TestPage.FIRST_CHILD_ID));
			add(new SecondChild());
		}
	}

	private static class SecondChild extends WebMarkupContainer
	{
		SecondChild()
		{
			super(TestPage.SECOND_CHILD_ID);
		}

		@Override
		protected void onInitialize()
		{
			super.onInitialize();

			getParent().addOrReplace(new WebMarkupContainer(TestPage.THIRD_CHILD_ID));
		}
	}
}
