package cn.edu.fudan.scanscheduler.util;

import org.eclipse.jdt.core.dom.*;

import java.io.*;

public class ASTUtil {

    private static int line=0;

    private static ASTParser astParser=ASTParser.newParser(AST.JLS8);

    private static CompilationUnit getCompilationUnit(String javaFilePath){
        try{
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
            byte[] input = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(input);
            bufferedInputStream.close();
            astParser.setSource(new String(input).toCharArray());
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        }catch(IOException e){
            e.printStackTrace();
        }
        return (CompilationUnit) (astParser.createAST(null));
    }

    private static String getFieldName(FieldDeclaration fd){
        Object o=fd.fragments().get(0);
        if(o instanceof  VariableDeclarationFragment){
            return ((VariableDeclarationFragment)o).getName().toString();
        }else{
            return fd.toString().trim();
        }
    }

    public static int getFiledLineNumber(String filePath,String className,String fieldName){
        CompilationUnit cu=getCompilationUnit(filePath);
        line=0;
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration type) {
                if(type.getName().toString().equals(className)){
                    for(FieldDeclaration field:type.getFields()){
                        if(fieldName.equals(getFieldName(field))){
                            line= cu.getLineNumber(field.getStartPosition());
                        }
                    }
                }
                return super.visit(type);
            }
        });
        return line;
    }

    public static String getCode(int startLine,int endLine,String filePath ){
        StringBuilder code=new StringBuilder();
        String s="";
        int line=1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while((s=bufferedReader.readLine())!=null){
                if(line>=startLine&&line<=endLine){
                    code.append(s);
                    code.append("/n");
                }
                line++;
                if(line>endLine)break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return code.toString();
    }

    public static String getFullCode(String filePath){
        StringBuilder code=new StringBuilder();
        String s="";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while((s=bufferedReader.readLine())!=null){
                code.append(s);
                code.append("/n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return code.toString();
    }

    public static void main(String[] args) {
        for(int i=1;i<10;i++){
            boolean flag=false;
            for(int j=8;j<16;j++){
                if(i==j){
                    flag=true;
                    System.out.println(i+"=="+j);
                    break;
                }
                System.out.println(j+"不等");
            }
            if(!flag){
                System.out.println(i+": 没找到");
            }
        }
    }
}
