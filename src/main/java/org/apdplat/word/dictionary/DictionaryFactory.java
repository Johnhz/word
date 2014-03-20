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

package org.apdplat.word.dictionary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 词典工厂
 * 通过系统属性指定词典实现类（dic.class）和词典文件（dic.path）
 * 默认实现词典实现类（org.apdplat.word.dictionary.impl.TrieV3）
 * 和词典文件（当前目录下的dic.txt）
 * @author 杨尚川
 */
public final class DictionaryFactory {
    private DictionaryFactory(){}
    public static final Dictionary getDictionary(){
        return DictionaryHolder.DIC;
    }
    private static final class DictionaryHolder{
        private static final Dictionary DIC;
        static{
            try {
                System.out.println("开始初始化词典");
                //选择词典实现，可以通过参数选择不同的实现
                String dicClass = System.getProperty("dic.class");
                if(dicClass == null){
                    dicClass = "org.apdplat.word.dictionary.impl.TrieV3";
                }
                DIC = (Dictionary)Class.forName(dicClass).newInstance();
                //选择词典
                String dicPath = System.getProperty("dic.path");
                if(dicPath == null){
                    dicPath = "dic.txt";
                }
                //统计词数
                int wordCount=0;
                //统计平均词长
                int totalLength=0;
                //统计词长分布
                Map<Integer,Integer> map = new HashMap<>();
                //这行代码够简洁
                List<String> lines = Files.readAllLines(Paths.get(dicPath), Charset.forName("utf-8"));
                //采用迭代器方式遍历，一边建Trie一边释放词典数据，以防止内存不够
                Iterator<String> iter = lines.iterator();
                while(iter.hasNext()){
                    wordCount++;
                    String line = iter.next();
                    //加入词典
                    DIC.add(line);
                    //统计不同长度的词的数目
                    int len = line.length();
                    totalLength+=len;
                    Integer value = map.get(len);
                    if(value==null){
                        value=1;
                    }else{
                        value++;
                    }
                    map.put(len, value);                    
                    //释放内存，上面已经一次性将词典加入内存，所以边加入词典边释放
                    iter.remove();
                }
                System.out.println("完成初始化词典，词数目："+wordCount);
                System.out.println("词典最大词长："+DIC.getMaxLength());
                System.out.println("词典平均词长："+(float)totalLength/wordCount);
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                System.err.println("词典装载失败:"+ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }
}