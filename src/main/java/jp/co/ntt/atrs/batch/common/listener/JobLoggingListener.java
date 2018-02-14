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
package jp.co.ntt.atrs.batch.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import jp.co.ntt.atrs.batch.common.logging.LogMessages;

@Component
public class JobLoggingListener implements JobExecutionListener {

    /**
     * γ‘γ?γ»γΌγΈεΊεγ«ε©η¨γγγ­γ°ζ©θ?½γζδΎγγγ€γ³γΏγγ§γΌγΉγ?
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobLoggingListener.class);

    /**
     * γΈγ§γιε§εε¦η?γ?
     * 
     * @param jobExecution
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // γΈγ§γγιε§γγγγ¨γγ­γ°γ«εΊεγγγ??
        LOGGER.info(LogMessages.I_AR_FW_L0001.getMessage(jobExecution.getJobInstance().getJobName()));
    }

    /**
     * γΈγ§γη΅δΊ?εΎε?¦η?γ?
     * 
     * @param jobExecution
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        // γΈγ§γγη΅δΊ?γγγγ¨γγ­γ°γ«εΊεγγγ??
        LOGGER.info(LogMessages.I_AR_FW_L0002.getMessage(jobExecution.getJobInstance().getJobName()));
    }

}
