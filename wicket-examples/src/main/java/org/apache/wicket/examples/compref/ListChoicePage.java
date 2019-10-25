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
package org.apache.wicket.examples.compref;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.examples.WicketExamplePage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;


/**
 * Page with examples on {@link org.apache.wicket.markup.html.form.ListChoice}.
 * 
 * @author Eelco Hillenius
 */
public class ListChoicePage extends WicketExamplePage
{
	/** available sites for selection. */
	private static final List<String> SITES = Arrays.asList("The Server Side", "Java Lobby", "Java.Net");

	/**
	 * Constructor
	 */
	public ListChoicePage()
	{
		final Input input = new Input();
		setDefaultModel(new CompoundPropertyModel<>(input));

		// Add a FeedbackPanel for displaying our messages
		FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
		add(feedbackPanel);

		// Add a form with an onSumbit implementation that sets a message
		Form<Void> form = new Form<Void>("form")
		{
			@Override
			protected void onSubmit()
			{
				info("input: " + input);
			}
		};
		add(form);

		// Add a list choice component that uses Input's 'site' property to
		// designate the
		// current selection, and that uses the SITES list for the available
		// options.
		ListChoice<String> listChoice = new ListChoice<>("site", SITES);
		listChoice.setMaxRows(4);
		form.add(listChoice);
	}

	/**
	 * Override base method to provide an explanation
	 */
	@Override
	protected void explain()
	{
		String html = new StringBuilder().append("<select wicket:id=\"site\">\n").append("    <option>site 1</option>\n").append("    <option>site 2</option>\n").append("</select>").toString();
		String code = new StringBuilder().append("private static final List SITES = Arrays.asList(new String[] { \"The Server Side\", \"Java Lobby\", \"Java.Net\" });\n").append("...\n").append("&nbsp;&nbsp;&nbsp;&nbsp;// Add a list choice component that uses the model object's 'site' property to designate the\n").append("&nbsp;&nbsp;&nbsp;&nbsp;// current selection, and that uses the SITES list for the available options.\n").append("&nbsp;&nbsp;&nbsp;&nbsp;ListChoice listChoice = new ListChoice(\"site\", SITES);\n").append("&nbsp;&nbsp;&nbsp;&nbsp;listChoice.setMaxRows(4);\n").append("&nbsp;&nbsp;&nbsp;&nbsp;form.add(listChoice);").toString();
		add(new ExplainPanel(html, code));

	}

	/** Simple data class that acts as a model for the input fields. */
	public static class Input implements IClusterable
	{
		/** the selected site. */
		public String site = SITES.get(0);

		@Override
		public String toString()
		{
			return new StringBuilder().append("site = '").append(site).append("'").toString();
		}
	}

}
