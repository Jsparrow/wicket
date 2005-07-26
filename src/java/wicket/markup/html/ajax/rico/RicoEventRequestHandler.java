/*
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.ajax.rico;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.html.HtmlHeaderContainer;
import wicket.markup.html.StaticResourceReference;
import wicket.markup.html.ajax.AbstractEventRequestHandler;

/**
 * Handles event requests using Rico.
 *
 * @author Eelco Hillenius
 */
public abstract class RicoEventRequestHandler extends AbstractEventRequestHandler
{
	/** log. */
	private static Log log = LogFactory.getLog(RicoEventRequestHandler.class);

	/** reference to prototype js. */
	private static final StaticResourceReference PROTOTYPE_REF;

	/** reference to rico js. */
	private static final StaticResourceReference RICO_REF;

	static
	{
		PROTOTYPE_REF = new StaticResourceReference(RicoEventRequestHandler.class, "prototype.js");
		RICO_REF = new StaticResourceReference(RicoEventRequestHandler.class, "rico.js");
	}
	
	/**
	 * Construct.
	 */
	public RicoEventRequestHandler()
	{
	}

	/**
	 * Gets the onload statement(s) for the body component.
	 * @return the onload statement(s) for the body component
	 */
	public final String getBodyOnloadInitContribution()
	{
		return "javascript:bodyOnLoad();";
	}

	/**
	 * Let this handler print out the needed header contributions.
	 * @param container
	 */
	public final void printHeadInitContribution(HtmlHeaderContainer container)
	{
		// add our basic javascript needs to the header
		addJsReference(container, PROTOTYPE_REF);
		addJsReference(container, RICO_REF);
	}

	/**
	 * Adds a javascript reference.
	 * @param container the header container
	 * @param ref reference to add
	 */
	private void addJsReference(HtmlHeaderContainer container, StaticResourceReference ref)
	{
		String url = container.getPage().urlFor(ref.getPath());
		String s = 
			"\t<script language=\"JavaScript\" type=\"text/javascript\" " +
			"src=\"" + url + "\"></script>\n" +
			"<script language=\"JavaScript\">\n" +
		    "\tvar onloads = new Array();\n" +
		    "\tfunction bodyOnLoad() {\n " +
		    "\t\tfor (var i = 0 ; i < onloads.length ; i++ )\n" +
		    "\t\t\tonloads[i]();\t" +
		    "\t}\n" +
		    "</script>;";
		container.getResponse().write(s);
	}
}
