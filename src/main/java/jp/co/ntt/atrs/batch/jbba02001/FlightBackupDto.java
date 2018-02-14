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
package jp.co.ntt.atrs.batch.jbba02001;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * フライト情報�?避オブジェクト�??
 * 
 * @author NTT 電電太�?
 */
public class FlightBackupDto {

    /**
     * 搭乗日�?
     */
    private Date departureDate;

    /**
     * 便名�??
     */
    private String flightName;

    /**
     * 搭乗クラスコード�??
     */
    private String boardingClassCd;

    /**
     * 運�?種別コード�??
     */
    private String fareTypeCd;

    /**
     * 空席数�?
     */
    private int vacantNum;

    /**
     * 搭乗日 を取得する�??
     * 
     * @return 搭乗日
     */
    public Date getDepartureDate() {
        return departureDate;
    }

    /**
     * 搭乗日 を設定する�??
     * 
     * @param departureDate 搭乗日
     */
    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    /**
     * 便�? を取得する�??
     * 
     * @return 便�?
     */
    public String getFlightName() {
        return flightName;
    }

    /**
     * 便�? を設定する�??
     * 
     * @param flightName 便�?
     */
    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    /**
     * 搭乗クラスコー�? を取得する�??
     * 
     * @return 搭乗クラスコー�?
     */
    public String getBoardingClassCd() {
        return boardingClassCd;
    }

    /**
     * 搭乗クラスコー�? を設定する�??
     * 
     * @param boardingClassCd 搭乗クラスコー�?
     */
    public void setBoardingClassCd(String boardingClassCd) {
        this.boardingClassCd = boardingClassCd;
    }

    /**
     * 運�?種別コー�? を取得する�??
     * 
     * @return 運�?種別コー�?
     */
    public String getFareTypeCd() {
        return fareTypeCd;
    }

    /**
     * 運�?種別コー�? を設定する�??
     * 
     * @param fareTypeCd 運�?種別コー�?
     */
    public void setFareTypeCd(String fareTypeCd) {
        this.fareTypeCd = fareTypeCd;
    }

    /**
     * 空席数 を取得する�??
     * 
     * @return 空席数
     */
    public int getVacantNum() {
        return vacantNum;
    }

    /**
     * 空席数 を設定する�??
     * 
     * @param vacantNum 空席数
     */
    public void setVacantNum(int vacantNum) {
        this.vacantNum = vacantNum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
