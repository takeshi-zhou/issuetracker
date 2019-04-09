package cn.edu.fudan.issueservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.WeakHashMap;

@Slf4j
@Component
public class ExecuteShellUtil {

    public Map developerLinesOfCode(String start, String end, String repoId) {
        Map<Object, Object> map = new WeakHashMap<>();
        return map;
    }

}
