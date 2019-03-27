package cn.edu.fudan.measureservice.handler;

import cn.edu.fudan.measureservice.domain.*;
import cn.edu.fudan.measureservice.domain.Object;
import cn.edu.fudan.measureservice.domain.Package;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class JavaNCSSResultHandler implements ResultHandler{

    @Value("${result.file.home}")
    private String resultFileHome;

    @Override
    public Measure handle(String resultFileName,String level) {
        Measure measure=new Measure();
        Total total=new Total();
        SAXReader reader=new SAXReader();
        try{
            Document doc=reader.read(new File(resultFileHome+resultFileName));
            Element root=doc.getRootElement();
            measure.setPackages(analyzePackages(root.element("packages"),total));
            if(level.equals("file")){
                measure.getPackages().setPackages(null);
            }
            measure.setTotal(total);
            measure.setObjects(analyzeObjects(root.element("objects")));
            measure.setFunctions(analyzeFunctions(root.element("functions")));
        }catch (Exception e){
            e.printStackTrace();
        }
        return measure;
    }

    @SuppressWarnings("unchecked")
    private Packages analyzePackages(Element packagesElement,Total total){
        Packages packages=new Packages();
        List<Package> packageList=new ArrayList<>();
        Iterator<Element> iterator=packagesElement.elementIterator("package");
        while(iterator.hasNext()){
            Element packageElement=iterator.next();
            Package i_package=new Package();
            i_package.setName(packageElement.elementText("name"));
            i_package.setClasses(Integer.valueOf(packageElement.elementText("classes")));
            i_package.setFunctions(Integer.valueOf(packageElement.elementText("functions")));
            i_package.setNcss(Integer.valueOf(packageElement.elementText("ncss")));
            i_package.setJavaDocs(Integer.valueOf(packageElement.elementText("javadocs")));
            i_package.setJavaDocsLines(Integer.valueOf(packageElement.elementText("javadoc_lines")));
            i_package.setSingleCommentLines(Integer.valueOf(packageElement.elementText("single_comment_lines")));
            i_package.setMultiCommentLines(Integer.valueOf(packageElement.elementText("multi_comment_lines")));
            packageList.add(i_package);
        }
        packages.setPackages(packageList);
        Element totalElement=packagesElement.element("total");
        total.setClasses(Integer.valueOf(totalElement.elementText("classes")));
        total.setFunctions(Integer.valueOf(totalElement.elementText("functions")));
        total.setNcss(Integer.valueOf(totalElement.elementText("ncss")));
        total.setJavaDocs(Integer.valueOf(totalElement.elementText("javadocs")));
        total.setJavaDocsLines(Integer.valueOf(totalElement.elementText("javadoc_lines")));
        total.setSingleCommentLines(Integer.valueOf(totalElement.elementText("single_comment_lines")));
        total.setMultiCommentLines(Integer.valueOf(totalElement.elementText("multi_comment_lines")));

        Element tableElement=packagesElement.element("table");
        packages.setPackageAverage(analyzePackageAverage(tableElement));
        return packages;
    }

    @SuppressWarnings("unchecked")
    private PackageAverage analyzePackageAverage(Element tableElement){
        PackageAverage packageAverage=new PackageAverage();
        int count1=1;
        Iterator<Element> trIterator=tableElement.elementIterator("tr");
        while (trIterator.hasNext()){
            Element packageTr=trIterator.next();
            if(count1++==3){
                int count2=1;
                Iterator<Element> tdIterator=packageTr.elementIterator("td");
                while(tdIterator.hasNext()){
                    Element tdElement=tdIterator.next();
                    String valueText=tdElement.getText();
                    if(valueText.contains(","))
                        valueText=valueText.replaceAll(",","");
                    switch (count2++){
                        case 2:
                            packageAverage.setClasses(Double.valueOf(valueText));
                            break;
                        case 3:
                            packageAverage.setFunctions(Double.valueOf(valueText));
                            break;
                        case 4:
                            packageAverage.setNcss(Double.valueOf(valueText));
                            break;
                        case 5:
                            packageAverage.setJavaDocs(Double.valueOf(valueText));
                            break;
                    }
                }
            }
        }
        return packageAverage;
    }

    @SuppressWarnings("unchecked")
    private Objects analyzeObjects(Element objectsElement){
        Objects objects=new Objects();
        List<Object> objectList=new ArrayList<>();
        Iterator<Element> iterator=objectsElement.elementIterator("object");
        while (iterator.hasNext()){
            Element objectElement=iterator.next();
            Object object=new Object();
            object.setName(objectElement.elementText("name"));
            object.setNcss(Integer.valueOf(objectElement.elementText("ncss")));
            object.setFunctions(Integer.valueOf(objectElement.elementText("functions")));
            object.setClasses(Integer.valueOf(objectElement.elementText("classes")));
            object.setJavaDocs(Integer.valueOf(objectElement.elementText("javadocs")));
            object.setJavaDocsLines(Integer.valueOf(objectElement.elementText("javadocs_lines")));
            object.setSingleCommentLines(Integer.valueOf(objectElement.elementText("single_comment_lines")));
            object.setImplementationCommentLines(Integer.valueOf(objectElement.elementText("implementation_comment_lines")));
            objectList.add(object);
        }
        Element averageElement=objectsElement.element("averages");
        ObjectAverage objectAverage=new ObjectAverage();
        objectAverage.setNcss(Double.valueOf(averageElement.elementText("ncss")));
        objectAverage.setFunctions(Double.valueOf(averageElement.elementText("functions")));
        objectAverage.setClasses(Double.valueOf(averageElement.elementText("classes")));
        objectAverage.setJavaDocs(Double.valueOf(averageElement.elementText("javadocs")));
        objectAverage.setJavaDocsLines(Double.valueOf(averageElement.elementText("javadocs_lines")));
        objectAverage.setSingleCommentLines(Double.valueOf(averageElement.elementText("single_comment_lines")));
        objectAverage.setImplementationCommentLines(Double.valueOf(averageElement.elementText("implementation_comment_lines")));

        objects.setObjectAverage(objectAverage);
        objects.setObjects(objectList);
        return objects;
    }

    @SuppressWarnings("unchecked")
    private Functions analyzeFunctions(Element functionsElement){
        Functions functions=new Functions();
        List<Function> functionList=new ArrayList<>();
        Iterator<Element> iterator=functionsElement.elementIterator("function");
        while(iterator.hasNext()){
            Element functionElement=iterator.next();
            Function function=new Function();
            function.setName(functionElement.elementText("name"));
            function.setNcss(Integer.valueOf(functionElement.elementText("ncss")));
            function.setCcn(Integer.valueOf(functionElement.elementText("ccn")));
            function.setJavaDocs(Integer.valueOf(functionElement.elementText("javadocs")));
            functionList.add(function);
        }
        Element averageElement=functionsElement.element("function_averages");
        FunctionAverage functionAverage=new FunctionAverage();
        functionAverage.setNcss(Double.valueOf(averageElement.elementText("ncss")));
        functionAverage.setCcn(Double.valueOf(averageElement.elementText("ccn")));
        functionAverage.setJavaDocs(Double.valueOf(averageElement.elementText("javadocs")));
        functions.setFunctionAverage(functionAverage);
        functions.setFunctions(functionList);
        return functions;
    }

}
