package cn.edu.fudan.measureservice.domain;

import java.util.List;

public class Functions {

    private List<Function> functions;

    private FunctionAverage functionAverage;

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public FunctionAverage getFunctionAverage() {
        return functionAverage;
    }

    public void setFunctionAverage(FunctionAverage functionAverage) {
        this.functionAverage = functionAverage;
    }
}
