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
package jp.co.ntt.atrs.batch.jbbb01002;

import java.util.List;

/**
 * åºéæ­ä¹è??é?è¨ã¸ã§ãã§ä½¿ç¨ããDAOã¤ã³ã¿ã¼ãã§ã¼ã¹ã?
 * 
 * @author NTT é»é»æ¬¡é?
 */
public interface JBBB01002Dao {
    /**
     * åºéæ­ä¹è??æ?å ±ãåå¾ããã??
     */
    List<RouteAggregationResultDto> findRouteAggregationByDepartureDateList();

}
