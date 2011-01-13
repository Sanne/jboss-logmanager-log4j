/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.logmanager.log4j.filters;

import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.log4j.ConvertedLoggingEvent;

import java.util.logging.LogRecord;
import org.apache.log4j.spi.Filter;

/**
 * A bridge filter to a log4j filter chain.
 */
public final class Log4jFilter implements java.util.logging.Filter {
    private final Filter filterChain;
    private final boolean defaultResult;

    /**
     * Construct a new instance.
     *
     * @param filterChain the log4j filter chain
     * @param defaultResult the result to use if the filter chain returns {@link Filter#NEUTRAL}
     */
    public Log4jFilter(final Filter filterChain, final boolean defaultResult) {
        this.filterChain = filterChain;
        this.defaultResult = defaultResult;
    }

    /**
     * Determine if the record is loggable.
     *
     * @param record the log record
     * @return {@code true} if it is loggable
     */
    public boolean isLoggable(final LogRecord record) {
        final ExtLogRecord extRec = ExtLogRecord.wrap(record);
        Filter filter = filterChain;
        while (filter != null) {
            final int result = filter.decide(new ConvertedLoggingEvent(extRec));
            switch (result) {
                case Filter.DENY: return false;
                case Filter.ACCEPT: return true;
            }
            filter = filter.getNext();
        }
        return defaultResult;
    }
}
