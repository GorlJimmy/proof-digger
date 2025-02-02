/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.linuxkernel.proof.digger.demo;

import java.util.ArrayList;
import java.util.List;

import org.linuxkernel.proof.digger.datasource.BaiduDataSource;
import org.linuxkernel.proof.digger.datasource.DataSource;
import org.linuxkernel.proof.digger.files.FilesConfig;
import org.linuxkernel.proof.digger.system.CommonIssueSolutionSystem;
import org.linuxkernel.proof.digger.system.IssueSolutionSystem;

/**
 * 从配置文件中读取问题 然后从baidu搜索证据 然后计算候选答案
 *
 * @author 杨尚川
 */
public class BaiduDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        files.add(FilesConfig.personNameQuestions);
        files.add(FilesConfig.locationNameQuestions);
        files.add(FilesConfig.organizationNameQuestions);
        files.add(FilesConfig.numberQuestions);
        files.add(FilesConfig.timeQuestions);
        DataSource dataSource = new BaiduDataSource(files);

        IssueSolutionSystem questionAnsweringSystem = new CommonIssueSolutionSystem();
        questionAnsweringSystem.setDataSource(dataSource);
        questionAnsweringSystem.answerQuestions();
        questionAnsweringSystem.showPerfectQuestions();
        questionAnsweringSystem.showNotPerfectQuestions();
        questionAnsweringSystem.showWrongQuestions();
        questionAnsweringSystem.showUnknownTypeQuestions();
    }
}