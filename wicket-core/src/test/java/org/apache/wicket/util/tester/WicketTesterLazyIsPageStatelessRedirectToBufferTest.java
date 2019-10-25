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
package org.apache.wicket.util.tester;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.mock.MockApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.RequestCycleSettings;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.junit.jupiter.api.Test;

/**
 * https://issues.apache.org/jira/browse/WICKET-5426
 */
public class WicketTesterLazyIsPageStatelessRedirectToBufferTest extends WicketTesterLazyIsPageStatelessBase
{
	@Override
	protected WebApplication newApplication()
	{
		return new MockApplication()
		{
			@Override
			public void init() {
				super.init();
				getRequestCycleSettings().setRenderStrategy(RequestCycleSettings.RenderStrategy.REDIRECT_TO_BUFFER);
			}
		};
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-5578
	 * 
	 * Listener interfaces for stateless component must be invoked also for 
	 * "lazy" stateless pages.
	 * 
	 * @throws Exception
	 */
	@Test
    void statelessListenerInvoked() throws Exception
	{
	    tester.startPage(StatelessListenerPage.class);
	    tester.assertRenderedPage(StatelessListenerPage.class);
	    
	    FormTester formTester = tester.newFormTester("statelessForm");
	    formTester.submit();
	    
	    tester.assertRenderedPage(EmptyPage.class);	    
	}
	
	public static class StatelessListenerPage extends MyPage
	{
		public StatelessListenerPage()
	    {
		add(new StatelessForm("statelessForm")
		{
			@Override
			protected void onSubmit() {
				setResponsePage(EmptyPage.class);
			}
		});
	    }
	    @Override
	    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass)
	    {
		return new StringResourceStream(new StringBuilder().append("<html>\n").append("<body>\n").append("\t<a wicket:id=\"link\" />\n").append("\t<form wicket:id=\"statelessForm\"></form>\n").append("\t<div wicket:id=\"isPageStateless\" />\n").append("</body>\n").append("</html>").toString());
	    }
	}
	
	public static class EmptyPage extends WebPage implements IMarkupResourceStreamProvider 
	{

		public EmptyPage()
		{
		}
		
		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			String pageMarkup = new StringBuilder().append("<html><head></head><body>").append("<div>").append("</div>").append("</body></html>").toString();
			return new StringResourceStream(pageMarkup);
		}
		
	}
}
