package com.lan.lineage.druid;

import com.alibaba.fastjson.JSONObject;
import com.lan.lineage.common.LineageColumn;
import com.lan.lineage.common.TreeNode;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lanxueri
 * @ClassName DruidTest
 * @Description TODO
 * @createTime 2020-07-31
 */
public class DruidTest {
    String file = this.getClass().getResource("/sql").getFile();
    public static void main(String[] args) throws IOException {

        File file = ResourceUtils.getFile(new DruidTest().file);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        bufferedReader.close();

        Map<String,String> map = new LinkedHashMap<>();
        Pattern pattern = Pattern.compile("##\\s.*\\S");
        Matcher matcher = pattern.matcher(sb.toString());
        List<String> list = Arrays.asList(sb.toString().split("##\\s.*\\S"));
        int j = 1;
        while (matcher.find()){
            String key = matcher.group().replace("##","").trim();
            map.put(key,list.get(j));
            j++;
        }

        String key = "sub sql";
        System.out.println("Begin parse:"+key+"\n"+"sql:"+map.get(key));

        LineageColumn root = new LineageColumn();
        TreeNode<LineageColumn> rootNode = new TreeNode<>(root);

        LineageUtils.columnLineageAnalyzer(map.get(key),rootNode);

        for (TreeNode<LineageColumn> e : rootNode.getChildren()) {
            Set<LineageColumn> leafNodes =  e.getAllLeafData();
            for (LineageColumn f : leafNodes){
                if (f.getIsEnd()){
                    System.out.println(e.getData().getTargetColumnName() + "\tfrom:"+ JSONObject.toJSONString(f)+"\n");
                }

            }

        }

        for (TreeNode<LineageColumn> node : rootNode) {

            StringBuilder indent = new StringBuilder();
            for (int i = 1; i < node.getLevel();i++){
                indent.append("     ");
            }

            System.out.println(indent.toString() + JSONObject.toJSONString(node.getData())+"\n");
        }




    }
}
