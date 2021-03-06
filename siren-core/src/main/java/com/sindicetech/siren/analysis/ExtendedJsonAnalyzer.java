/**
 * Copyright (c) 2014, Sindice Limited. All Rights Reserved.
 *
 * This file is part of the SIREn project.
 *
 * SIREn is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * SIREn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sindicetech.siren.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.util.Version;

import com.sindicetech.siren.analysis.filter.DatatypeAnalyzerFilter;
import com.sindicetech.siren.analysis.filter.PositionAttributeFilter;
import com.sindicetech.siren.analysis.filter.SirenPayloadFilter;
import com.sindicetech.siren.util.JSONDatatype;
import com.sindicetech.siren.util.XSDDatatype;

import java.io.Reader;
import java.util.Map.Entry;

/**
 * The ExtendedJsonAnalyzer is especially designed to process JSON data.
 */
public class ExtendedJsonAnalyzer extends Analyzer {

  protected Analyzer                     valueAnalyzer;
  protected Analyzer                     fieldAnalyzer;

  protected final CharArrayMap<Analyzer> regAnalyzers;

  /**
   * Create a {@link ExtendedJsonAnalyzer} with the specified {@link Analyzer}s for
   * field names and values.
   * <p>
   * The default analyzer for field names will be associated with the datatype
   * {@link JSONDatatype#JSON_FIELD}. The default analyzer for values will be
   * associated with the datatype {@link XSDDatatype#XSD_STRING}.
   *
   * @param fieldAnalyzer Default {@link Analyzer} for the field names
   * @param valueAnalyzer Default {@link Analyzer} for the values
   */
  public ExtendedJsonAnalyzer(final Analyzer fieldAnalyzer,
                              final Analyzer valueAnalyzer) {
    this.valueAnalyzer = valueAnalyzer;
    this.fieldAnalyzer = fieldAnalyzer;
    // here, we just need to indicate a version > Lucene 3.1 - see CharArrayMap
    regAnalyzers = new CharArrayMap<Analyzer>(Version.LUCENE_46, 64, false);

  }

  public void setValueAnalyzer(final Analyzer analyzer) {
    valueAnalyzer = analyzer;
  }

  public void setFieldAnalyzer(final Analyzer analyzer) {
    fieldAnalyzer = analyzer;
  }

  /**
   * Assign an {@link Analyzer} to be used with that key. That analyzer is used
   * to process tokens generated by the {@link ExtendedJsonTokenizer}.
   *
   * @param datatype The datatype key
   * @param a the associated {@link Analyzer}
   */
  public void registerDatatype(final char[] datatype, final Analyzer a) {
    if (!regAnalyzers.containsKey(datatype)) {
      regAnalyzers.put(datatype, a);
    }
  }

  /**
   * Remove all registered Datatype {@link Analyzer}s.
   */
  public void clearDatatypes() {
    regAnalyzers.clear();
  }

  @Override
  protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
    final ExtendedJsonTokenizer source = new ExtendedJsonTokenizer(reader);

    final DatatypeAnalyzerFilter tt = new DatatypeAnalyzerFilter(source, fieldAnalyzer, valueAnalyzer);
    for (final Entry<Object, Analyzer> e : regAnalyzers.entrySet()) {
      tt.register((char[]) e.getKey(), e.getValue());
    }
    TokenStream sink = new PositionAttributeFilter(tt);
    sink = new SirenPayloadFilter(sink);
    return new TokenStreamComponents(source, sink);
  }

}
