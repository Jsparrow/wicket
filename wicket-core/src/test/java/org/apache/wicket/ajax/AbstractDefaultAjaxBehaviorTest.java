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
package org.apache.wicket.ajax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxAttributeName;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for AbstractDefaultAjaxBehavior
 * 
 * @since 6.0
 */
class AbstractDefaultAjaxBehaviorTest
{
	/**
	 * Checks the generated JSON for Ajax's attributes
	 */
	@Test
	void renderAjaxAttributes()
	{
		AjaxRequestAttributes attributes = new AjaxRequestAttributes();
		attributes.getExtraParameters().put("param1", 123);
		attributes.getExtraParameters().put("param2", Locale.CANADA_FRENCH);

		AjaxCallListener listener = new AjaxCallListener();
		listener.onPrecondition("return somePrecondition();");
		listener.onBefore("alert('Before!');");
		listener.onAfter("alert('After!');");
		listener.onSuccess("alert('Success!');");
		listener.onFailure("alert('Failure!');");
		listener.onComplete("alert('Complete!');");
		attributes.getAjaxCallListeners().add(listener);

		Component component = Mockito.mock(Component.class);
		AbstractDefaultAjaxBehavior behavior = new AbstractDefaultAjaxBehavior()
		{
			@Override
			protected void respond(AjaxRequestTarget target)
			{
			}

			@Override
			public CharSequence getCallbackUrl()
			{
				return "some/url";
			}
		};
		behavior.bind(component);

		CharSequence json = behavior.renderAjaxAttributes(component, attributes);

		String expected = new StringBuilder().append("{\"").append(AjaxAttributeName.URL).append("\":\"some/url\",\"").append(AjaxAttributeName.BEFORE_HANDLER).append("\":[function(attrs){alert('Before!');}],\"")
				.append(AjaxAttributeName.AFTER_HANDLER).append("\":[function(attrs){alert('After!');}],\"").append(AjaxAttributeName.SUCCESS_HANDLER).append("\":[function(attrs, jqXHR, data, textStatus){alert('Success!');}],\"").append(AjaxAttributeName.FAILURE_HANDLER)
				.append("\":[function(attrs, jqXHR, errorMessage, textStatus){alert('Failure!');}],\"").append(AjaxAttributeName.COMPLETE_HANDLER).append("\":[function(attrs, jqXHR, textStatus){alert('Complete!');}],\"").append(AjaxAttributeName.PRECONDITION).append("\":[function(attrs){return somePrecondition();}],\"")
				.append(AjaxAttributeName.EXTRA_PARAMETERS).append("\":[{\"name\":\"param1\",\"value\":123},{\"name\":\"param2\",\"value\":\"fr_CA\"}]").append("}").toString();

		assertEquals(expected, json);
	}
}
