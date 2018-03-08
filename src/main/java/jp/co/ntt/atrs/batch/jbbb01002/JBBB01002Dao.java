/*
 * Copyright 2014-2018 NTT Corporation.
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
 * 区間搭乗者集計ジョブで使用するDAOインターフェース。
 * 
 * @author NTT 電電次郎
 */
public interface JBBB01002Dao {
    /**
     * 区間搭乗者情報を取得する。
     */
    List<RouteAggregationResultDto> findRouteAggregationByDepartureDateList();

}
