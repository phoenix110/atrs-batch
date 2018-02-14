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
package jp.co.ntt.atrs.batch.jbbb01001;

import jp.co.ntt.atrs.batch.common.exception.AtrsBatchException;
import jp.co.ntt.atrs.batch.common.logging.LogMessages;
import jp.co.ntt.atrs.batch.jbbb00.AggregationPeriodDto;
import jp.co.ntt.atrs.batch.jbbb00.AggregationPeriodUtil;

import org.dozer.Mapper;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;

import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

/**
 * äºç´?æ?å ±ãéè¨ããäºç´?é?è¨æå ±ãCSVãã¡ã¤ã«ã«åºåããã??
 * 
 * @author é»é»æ¬¡é?
 */
@Component("JBBB01001Tasklet")
@Scope("step")
public class JBBB01001Tasklet implements Tasklet {
    /**
     * ã¡ã?ã»ã¼ã¸åºåã«å©ç¨ããã­ã°æ©è?½ãæä¾ããã¤ã³ã¿ãã§ã¼ã¹ã?
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JBBB01001Tasklet.class);

    /**
     * äºç´?æ?å ±é?è¨çµæå¥åç¨reader?¼ã³ã³ãã­ã¼ã«ãã¬ã¤ã¯ç¨?¼ã??
     */
    @Inject
    SingleItemPeekableItemReader<ReservationResultDto> reservationResultReader;

    /**
     * äºç´?æ?å ±é?è¨å?ºåç¨writerã?
     */
    @Inject
    ItemStreamWriter<ReservationDto> reservationWriter;

    /**
     * å¥åãã§ã?ã¯ç¨ã®ããªã?ã¼ã¿ã?
     */
    @Inject
    Validator<ReservationResultDto> validator;

    /**
     * ã¡ã?ã»ã¼ã¸ç®¡ç?æ©è?½ã?
     */
    @Inject
    MessageSource messageSource;

    /**
     * Beanãããã?¼ã?
     */
    @Inject
    Mapper beanMapper;

    /**
     * ã¸ã§ããã©ã¡ã¼ã¿ é?è¨éå§æ¥ã?
     */
    @Value("#{jobParameters['firstDateStr']}")
    private String firstDateStr;

    /**
     * ã¸ã§ããã©ã¡ã¼ã¿ é?è¨çµäº?æ¥ã?
     */
    @Value("#{jobParameters['lastDateStr']}")
    private String lastDateStr;

    /**
     * ã¦ã¼ã¶ã¼ã®ç¾å¨ã®ä½æ¥­ã?ã£ã¬ã¯ããªã?
     */
    @Value("${user.dir}")
    private String userDir;

    /**
     * äºç´?æ?å ±é?è¨çµæãã¡ã¤ã«ãã¹ã?
     */
    @Value("${path.ReservationData}")
    private String PATH_RESERVATION_DATA;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        // é?è¨æéã?®åå¾ã?»è¨­å®?
        AggregationPeriodDto aggregationPeriod = AggregationPeriodUtil.create(firstDateStr, lastDateStr);

        if (aggregationPeriod == null) {
            // é?è¨æéã?®åå¾ã§ä¾å¤ï¼?100:ç°å¸¸çµäº??¼?
            contribution.setExitStatus(new ExitStatus("BUSINESS_ERROR"));
            return RepeatStatus.FINISHED;
        }

        // åºåãã¡ã¤ã«ãã¹ã¨åºåä»¶æ°ãä¿æããMapã?
        Map<String, Integer> outputLineCountMap = new LinkedHashMap<>();

        try {
            // æ¤ç´¢æ¡ä»¶æ?å®?
            executionContext.put("firstDate", aggregationPeriod.getFirstDate());
            executionContext.put("lastDate", aggregationPeriod.getLastDate());

            // å¥åãã¡ã¤ã«ãªã¼ãã³
            reservationResultReader.open(executionContext);

            // åºåä»¶æ°
            int dataCount = 0;
            int outputLineCount = 0;

            // å¦ç?ä¸­ã®åºåå?ãã¡ã¤ã«ãã¹
            Path outputFile = Paths.get(userDir, PATH_RESERVATION_DATA);

            // ã³ã³ãã­ã¼ã«ãã¬ã¤ã¯ã®å¦ç?çµæãå«ããã°ã«ã¼ãåä½ã?®ã¬ã³ã¼ããæ ¼ç´ãã?
            List<ReservationDto> items = new ArrayList<>();

            // æ¬¡è¦ç´?ã«ã?ã¼ã¿ãå­å¨ããã¾ã§å¦ç?ãç¹°ãè¿ã
            while (reservationResultReader.peek() != null) {
                dataCount++;
                outputLineCount++;

                // äºç´?æ?å ±ãåå¾ãã?
                ReservationResultDto inputData = reservationResultReader.read();

                // å¥åãã§ã?ã¯ã¨ã©ã¼ãã³ããªã³ã°
                try {
                    validator.validate(inputData);
                } catch (ValidationException e) {
                    // FieldErrorsã®åæ°å?ãä»¥ä¸ã?®å¦ç?ãç¹°ãè¿ã
                    for (FieldError fieldError : ((BindException) e.getCause()).getFieldErrors()) {
                        // å¥åãã§ã?ã¯ã¨ã©ã¼ã¡ã?ã»ã¼ã¸ãå?ºå?
                        LOGGER.warn(messageSource.getMessage(fieldError, null) + "[" + fieldError.getRejectedValue()
                                + "]" + "(" + inputData.toString() + ")");
                    }

                    // å¥åãã§ã?ã¯ã¨ã©ã¼?¼?100:ç°å¸¸çµäº??¼?
                    LOGGER.error(LogMessages.E_AR_FW_L9003.getMessage(), e);
                    contribution.setExitStatus(new ExitStatus("BUSINESS_ERROR"));
                    return RepeatStatus.FINISHED;
                }

                // DTOã®è©°ãæ¿ãå?¦ç?
                ReservationDto printData = beanMapper.map(inputData, ReservationDto.class);

                items.add(printData);

                // æ¬¡ã®ã¬ã³ã¼ããåèª­ã¿ãã
                ReservationResultDto nextData = reservationResultReader.peek();

                // å¯¾è±¡ã¬ã³ã¼ãå?¦ç?å¾ã«ã³ã³ãã­ã¼ã«ãã¬ã¤ã¯ãå®æ½ãã
                if (isBreakByReserveDate(nextData, inputData)) {
                    // ã³ã³ãã­ã¼ã«ãã¬ã¤ã¯ããå¤ãåå¾ããå?ºåãã¡ã¤ã«ãã?¼ã?ç¨ã«æ?å­å?å¤æãã
                    LocalDate ld = new LocalDate(inputData.getReserveDate());
                    String outputDate = ld.toString("yyyyMMdd");

                    // åºåãã¡ã¤ã«ãã¹
                    String outputFilePath = MessageFormat.format(outputFile.toString(), outputDate);

                    try {
                        // åºåãã¡ã¤ã«ãªã¼ãã³
                        reservationWriter.open(executionContext);

                        // æ¸ãè¾¼ã¿
                        reservationWriter.write(items);
                        items.clear();

                        // å?ãã¡ã¤ã«ã®åºåä»¶æ°ãä¿æãã
                        outputLineCountMap.put(outputFilePath, outputLineCount);

                        // åºåä»¶æ°åæå?
                        outputLineCount = 0;
                    } catch (ItemStreamException e) {
                        // ãã¡ã¤ã«ãªã¼ãã³ã¨ã©ã¼
                        LOGGER.error(LogMessages.E_AR_FW_L9006.getMessage());
                        throw new AtrsBatchException(e);
                    } catch (Exception e) {
                        // ãã¡ã¤ã«æ¸è¾¼ã¿ã¨ã©ã¼?¼?100:ç°å¸¸çµäº??¼?
                        LOGGER.error(LogMessages.E_AR_FW_L9001.getMessage(), e);
                        contribution.setExitStatus(new ExitStatus("BUSINESS_ERROR"));
                        return RepeatStatus.FINISHED;
                    } finally {
                        try {
                            // åºåãã¡ã¤ã«ã¯ã­ã¼ãº
                            reservationWriter.close();
                        } catch (ItemStreamException e) {
                            // ã¯ã­ã¼ãºå¤±æ?
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("ã¯ã­ã¼ãºå¤±æ?", e);
                            }
                        }
                    }
                    
                    try {
                        // ãªãã?¼ã?
                        Files.move(outputFile, Paths.get(outputFilePath));
                    } catch (IOException e) {
                        // ãã¡ã¤ã«ãªãã?¼ã?å¤±æ?
                        LOGGER.error(LogMessages.E_AR_FW_L9009.getMessage(outputFile.toString(), outputFilePath), e);
                        throw new AtrsBatchException(e);
                    }
                }
            }

            // åå¾ä»¶æ°ã?0ä»¶ã®å ´åã­ã°ãå?ºåããã??
            if (dataCount == 0) {
                LOGGER.warn(LogMessages.W_AR_BB01_L2001.getMessage());
                // ã¸ã§ãçµäº?ã³ã¼ãï¼?2:æ­£å¸¸çµäº??¼?
                contribution.setExitStatus(new ExitStatus("NORMAL_NONE_TARGET"));
                return RepeatStatus.FINISHED;
            }
        } catch (ItemStreamException e) {
            // ãã¡ã¤ã«ãªã¼ãã³ã¨ã©ã¼
            LOGGER.error(LogMessages.E_AR_FW_L9006.getMessage());
            throw new AtrsBatchException(e);
        } finally {
            try {
                // å¥åãã¡ã¤ã«ã¯ã­ã¼ãº
                reservationResultReader.close();
            } catch (ItemStreamException e) {
                // ã¯ã­ã¼ãºå¤±æ?
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("ã¯ã­ã¼ãºå¤±æ?", e);
                }
            }
        }

        // åºåå?ãã¡ã¤ã«ã¨ãã¡ã¤ã«åºåä»¶æ°ãã­ã°ã«åºå?
        for (Entry<String, Integer> entry : outputLineCountMap.entrySet()) {
            LOGGER.info(LogMessages.I_AR_FW_L0003.getMessage(entry.getKey(), entry.getValue()));
        }

        // ã¸ã§ãçµäº?ã³ã¼ãï¼?0:æ­£å¸¸çµäº??¼?
        contribution.setExitStatus(new ExitStatus("NORMAL"));
        return RepeatStatus.FINISHED;
    }

    /**
     * æ¬¡è¦ç´?ã®ã?ã¼ã¿ã¨ç¾å¨ã®ã?ã¼ã¿ã¨ãæ¯è¼?ãã?trueã¾ãã?¯falseãè¿ãã?
     * 
     * @param nextData æ¬¡ã®ã?ã¼ã¿
     * @param inputData ç¾å¨ã®ã?ã¼ã¿
     * @return true or false
     */
    private boolean isBreakByReserveDate(ReservationResultDto nextData, ReservationResultDto inputData) {
        return (nextData == null || nextData.getReserveDate().compareTo(inputData.getReserveDate()) != 0);
    }
}
