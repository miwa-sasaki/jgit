package org.eclipse.jgit.diff;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;

//本質解析
/**
 * @author miwaaa8
 *
 */
public class AstVisitor extends ASTVisitor {

	CompilationUnit compilationUnit;
	private String[] source;
	static String ast = new String("");


	// コンストラクタ
	/**
	 * @param compilationUnit
	 * @param source
	 */
	public AstVisitor(CompilationUnit compilationUnit, String[] source) {
		super();
		this.compilationUnit = compilationUnit;
		this.source = source;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(CompilationUnit node) {
		//int startLineNumber = compilationUnit.getLineNumber(node.getStartPosition()) -1;

		for (Comment comment : (List<Comment>) node.getCommentList()) {
			comment.accept(new CommentVisitor(node, source));
		}
		//コメント以外全部書き出す
		//行数含む
		//ast += startLineNumber + ":" + node + "\n";
		ast += node + "\n";
		//System.out.print(ast);

		return super.visit(node);
	}


	//javadoc消す
	@Override
	public boolean visit(Javadoc node) {
		//nodeをstringに変換
		String nodeS = String.valueOf(node);

		//astを改行で分割して配列に格納
		String[] astSplit = ast.split("\n");
		//nodeつまりjavadocも格納
		String[] jdSplit = nodeS.split("\n");

	    int i = 0; /* 注目しているastSplitの位置 */
	    int j = 0; /* 注目しているjdSplitの位置 */
	    int ast_len, jd_len;
	    ast_len = astSplit.length;
	    jd_len = jdSplit.length;

	    /* テキストの最後尾に行き当たるか、パターンが見つかるまで繰り返す */
	    while ( i < ast_len && j < jd_len ) {
	        if(astSplit[i].endsWith(jdSplit[j]) || astSplit[i].startsWith(jdSplit[j])
	        		|| jdSplit[j].endsWith(astSplit[i]) || jdSplit[j].startsWith(astSplit[i])){
	        	i++;
	        	j++;
	        } else {
	        	//System.out.println(astSplit[i] + "\n" + jdSplit[j]);
	        	i++;
	        	j=0;
	        }
	    }
	     if(j == jd_len){//見つかった
	    	 //System.out.println("見つけた");
	    	 //各行に改行を戻す
	    	 for(int l = 0; l < astSplit.length; l++){
	    		 astSplit[l] += "\n";
	    	 }
	    	 //javadocの開始行
	    	 int startIndex = i-j;
	    	 int endIndex = j;
	    	 //javadocの部分を消す
	    	 for(int l = 0; l < endIndex; l++){
	    		 //System.out.println("aS[l] is " + astSplit[startIndex + l]);
	    		 astSplit[startIndex + l] = "";
	    		 //System.out.println("aS[l] is " + astSplit[startIndex + l]);
	    	 }

	    	 //astに戻す
	    	 ast = "";
	    	 for(int l = 0; l < astSplit.length; l++){
	    		 ast += astSplit[l];
	    	 }
	     } else {
	    	 System.out.println("javadoc見つからん");
	     }

	return true;
	}
}