package cn.edu.fudan.cloneservice.util;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.Set;

public class ASTUtil {

    private static int line = 0;

    private static ASTParser astParser = ASTParser.newParser(AST.JLS8);

    private static CompilationUnit getCompilationUnit(String javaFilePath) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(javaFilePath));
            byte[] input = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(input);
            bufferedInputStream.close();
            astParser.setSource(new String(input).toCharArray());
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (CompilationUnit) (astParser.createAST(null));
    }

    private static String getFieldName(FieldDeclaration fd) {
        Object o = fd.fragments().get(0);
        if (o instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) o).getName().toString();
        } else {
            return fd.toString().trim();
        }
    }

    public static int getFiledLineNumber(String filePath, String className, String fieldName) {
        CompilationUnit cu = getCompilationUnit(filePath);
        line = 0;
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration type) {
                if (type.getName().toString().equals(className)) {
                    for (FieldDeclaration field : type.getFields()) {
                        if (fieldName.equals(getFieldName(field))) {
                            line = cu.getLineNumber(field.getStartPosition());
                        }
                    }
                }
                return super.visit(type);
            }
        });
        return line;
    }


    public static String getCode(int startLine, int endLine, String filePath) {
        StringBuilder code = new StringBuilder();
        String s = "";
        int line = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                if (line >= startLine && line <= endLine) {
                    code.append(s);
                    code.append("/n");
                }
                line++;
                if (line > endLine) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    public static String getCodeAtSpecificLines(Set<String> lines, String filePath) {
        StringBuilder code = new StringBuilder();
        String s = "";
        int line = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                if (lines.contains(String.valueOf(line))) {
                    code.append(s);
                    code.append("/n");
                }
                line++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    public static String getFullCode(String filePath) {
        StringBuilder code = new StringBuilder();
        String s = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            while ((s = bufferedReader.readLine()) != null) {
                code.append(s);
                code.append("/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code.toString();
    }

    public static String getCodeFormSmb(int startLine,int endLine,String filePath){
        String url=filePath.substring(0,filePath.lastIndexOf('/')+1);
        String fileName=filePath.substring(filePath.lastIndexOf('/')+1);
        StringBuilder code = new StringBuilder();
        String s = "";
        int line = 1;
        SmbFileInputStream smbFileInputStream = null;
        try {
            SmbFile shareDir = new SmbFile(url);
            if(shareDir.isDirectory()){
                SmbFile []fileList = shareDir.listFiles();
                for(SmbFile file:fileList){
                    if(file.isFile()&&file.getName().equals(fileName)){
                        smbFileInputStream=new SmbFileInputStream(file);
                        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(smbFileInputStream));
                        while ((s = bufferedReader.readLine()) != null) {
                            if (line >= startLine && line <= endLine) {
                                code.append(s);
                                code.append("/n");
                            }
                            line++;
                            if (line > endLine) break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(smbFileInputStream!=null){
                try {
                    smbFileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return code.toString();
    }

    public static void main(String[] args) {
        System.out.println(getCodeFormSmb(5,10,"smb://fdse:smbcloudfdse@10.141.221.80/Share/test/testRepo/test.java"));
    }
}
