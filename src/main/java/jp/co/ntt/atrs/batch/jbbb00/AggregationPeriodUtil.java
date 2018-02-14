/*
 * Copyright 2014-2017 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jp.co.ntt.atrs.batch.jbbb00;

import jp.co.ntt.atrs.batch.common.logging.LogMessages;
import jp.co.ntt.atrs.batch.common.util.DateUtil;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * é?è¨æéã«é¢ããã¦ã¼ã?ã£ãªã?ã£ã¯ã©ã¹ã?
 * 
 * @author é»é» æ¬¡é?
 */
public class AggregationPeriodUtil {

    /**
     * ã¡ã?ã»ã¼ã¸åºåã«å©ç¨ããã­ã°æ©è?½ãæä¾ããã¤ã³ã¿ãã§ã¼ã¹ã?
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AggregationPeriodUtil.class);

    /**
     * ã³ã³ã¹ãã©ã¯ã¿ã?
     */
    private AggregationPeriodUtil() {
        // do nothing.
    }

    /**
     * é?è¨æéãªãã¸ã§ã¯ããçæ?ããã??
     * 
     * @param firstDateStr é?è¨éå§æ¥(yyyyMMddã®æ¥ä»æå­å??)
     * @param lastDateStr é?è¨çµäº?æ¥(yyyyMMddã®æ¥ä»æå­å??)
     * @return é?è¨æéã?å¼æ°ãæ¥ä»ã«å¤æã§ããªã?å ´åã?ã¾ãã?¯é?è¨å¯è½æéå?ã§ãªã?å ´åã?¯nullã?
     */
    public static AggregationPeriodDto create(String firstDateStr, String lastDateStr) {

        Date firstDate = null;
        Date lastDate = null;
        try {
            // æ¥ä»æå­å?ãDateåã«å¤æ
            firstDate = DateUtil.convertDate(firstDateStr);
            lastDate = DateUtil.convertDate(lastDateStr);
        } catch (IllegalArgumentException e) {
            // åå¤æã¨ã©ã¼
            LOGGER.error(LogMessages.E_AR_FW_L9005.getMessage(), e);
            return null;
        }

        // é?è¨å¯è½æéã§ãããã?®ãã§ã?ã¯ã?
        if (check(firstDate, lastDate)) {
            return new AggregationPeriodDto(firstDate, lastDate);
        }

        return null;
    }

    /**
     * é?è¨æéãé?è¨å¯è½æéã§ããããå¤å®ããã??
     * 
     * @param firstDate é?è¨éå§æ¥
     * @param lastDate é?è¨çµäº?æ¥
     * @return å¤å®çµæ
     */
    private static boolean check(Date firstDate, Date lastDate) {

        // é?è¨éå§æ¥ãçµäº?æ¥ã®Intervalä½æ??
        DateTime firstDateTime = new DateTime(firstDate);
        DateTime lastDateTime = new DateTime(lastDate);
        Interval interval = null;
        try {
            interval = new Interval(firstDateTime, lastDateTime);
        } catch (IllegalArgumentException e) {
            // æ¥ä»ãã§ã?ã¯ã¨ã©ã¼
            LOGGER.error(LogMessages.E_AR_BB01_L8001.getMessage(), e);
            return false;
        }

        // åç?§å¯è½æéã®ä½æ??
        DateTime currentDate = new DateTime().withTimeAtStartOfDay();
        DateTime firstFindAvailableDate = currentDate.minusMonths(1).dayOfMonth().withMinimumValue();
        DateTime lastFindAvailableDate = currentDate.plusMillis(1);
        Interval findAvailableInterval = new Interval(firstFindAvailableDate, lastFindAvailableDate);

        if (findAvailableInterval.contains(interval)) {
            return true;
        }
        // æ¥ä»ãã§ã?ã¯ã¨ã©ã¼
        LOGGER.error(LogMessages.E_AR_BB01_L8001.getMessage());
        return false;
    }

}
