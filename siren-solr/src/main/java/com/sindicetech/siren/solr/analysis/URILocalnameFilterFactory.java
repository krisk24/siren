/**
 * Copyright (c) 2014, Sindice Limited. All Rights Reserved.
 *
 * This file is part of the SIREn project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sindicetech.siren.solr.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import com.sindicetech.siren.analysis.filter.URILocalnameFilter;

import java.util.Map;

/**
 * Factory for {@link URILocalnameFilter}.
 *
 * <p>
 *
 * The property {@value #MAXLENGTH_KEY} can be set to define the maximum length
 * for a localname to be tokenized. The default positionIncrement value is
 * {@value URILocalnameFilter#DEFAULT_MAX_LENGTH}.
 */
public class URILocalnameFilterFactory extends TokenFilterFactory {

  public static final String MAXLENGTH_KEY = "maxLength";

  private int maxLength = 0;

  /**
   * Initialize this factory via a set of key-value pairs.
   */
  public URILocalnameFilterFactory(final Map<String,String> args) {
    super(args);
    maxLength = getInt(args, MAXLENGTH_KEY, URILocalnameFilter.DEFAULT_MAX_LENGTH);
    if (!args.isEmpty()) {
      throw new IllegalArgumentException("Unknown parameters: " + args);
    }
  }

  @Override
  public TokenStream create(final TokenStream input) {
    final URILocalnameFilter filter = new URILocalnameFilter(input);
    filter.setMaxLength(maxLength);
    return filter;
  }

}
