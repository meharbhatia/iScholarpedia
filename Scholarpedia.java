import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
        
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Scholarpedia extends HttpServlet  {

    public Connection conn;
    public Statement stmt;
        
    String vSearch = null;
    String vSearch_1 = null;
	
    public Scholarpedia() {
	super();
    }

    @Override
    public void init() throws ServletException {
		
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3300/iitb", "root", " ");
            System.out.println("connection opened");
            stmt = conn.createStatement();
	} 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
        
        PrintWriter pw = response.getWriter();
        try{
            vSearch_1 = request.getParameter("varColumnValue");
            process(request, response);
        }
        catch(SQLException se) {
            pw.println("SQL Eception " +se.getMessage());
        }
        catch(Exception e){
            pw.println(" " +e.getMessage());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        
        PrintWriter pw = response.getWriter();
        
        try{
            vSearch_1 = request.getParameter("varColumnValue");
            process(request, response);
        }
        catch(Exception e){
            pw.println(" " + e.getMessage());
        }
    }
    
    @Override
    public void destroy() {
	try {
            conn.close();
            conn = null;
            System.out.println("Connection closed");
        } 
        catch (Exception e) {

	}
                
    }
        
    public void process(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException, SQLException {
        
        PrintWriter pw = response.getWriter();
	int pageNumber = 0;
	int totalNumberOfRecords = 0;
	int recordPerPage = 10;
	int startIndex = 0;
	int numberOfPages = 0;
	
        String sPageNo = request.getParameter("pageno");
	pageNumber = Integer.parseInt(sPageNo);
	
        try {
            startIndex = (pageNumber * recordPerPage) - recordPerPage + 1;
		pw.println("<html>");
                pw.println("<head>");
                pw.println("<title> Scholarpedia </title>");
      
                pw.println("<style> table {width:100%; },th, tbody th:nth-child(even) { background: #F9966B; } tbody th:nth-child(odd) { background:#F9966B; } tbody th: 5th-child {background: white;}, tr:nth-of-type(odd){\n" +
                "  border-spacing: 0px 3px;  </style>");
                pw.println("</head>");
      
                pw.println("<body>");
                        
                vSearch = vSearch_1;
                request.setAttribute("varColumnValue", vSearch);
                        
                String sqlQuery;
                if (vSearch == null) {
                    sqlQuery = "SELECT search_criteria FROM search_criteria_data";
                    ResultSet rs_search = stmt.executeQuery(sqlQuery);   
                    rs_search.next();
                    vSearch = rs_search.getString("search_criteria");
                }
                
                else{
                    stmt.executeUpdate("update search_criteria_data set search_criteria ='"+vSearch+"'");
                }
                
                pw.println("<div align=\"left\">");
                pw.print("<img src='logo.jpg' HEIGHT=\"80\" WIDTH=\"250\" BORDER=\"0\"/>");
                pw.print("<h2>Search Criteria: "+vSearch + "</h2>");
                pw.println("</div>");
                                
                sqlQuery = "SELECT cm.ID, Paper_Title, Source_Title, Year, Authors, Authors_Af, Paper_ID, Ref_Link FROM conf_main cm, conf_main_addon cma";
                sqlQuery += " WHERE cm.ID=cma.Paper_ID ";
                
                if (vSearch.length() != 0) {
                    sqlQuery += " AND (lower(paper_title) like '%"+vSearch.toLowerCase()+"%'";
                    sqlQuery += " OR lower(source_title) like '%"+vSearch.toLowerCase()+"%'";
                    sqlQuery += " OR lower(year) like '%"+vSearch.toLowerCase()+"%'";
                    sqlQuery += " OR lower(authors) like '%"+vSearch.toLowerCase()+"%'";
                    sqlQuery += " OR lower(authors_af) like '%"+vSearch.toLowerCase()+"%')";
                }
                        
                ResultSet rs1 = stmt.executeQuery(sqlQuery);
                
                pw.println("<table frame=\"box\">");
                pw.println("<table cellpadding=\"4\">");
                pw.println("<col style=\"width:30%\">");
                pw.println("<col style=\"width:70%\">");
		
		rs1.absolute(startIndex);
		
                int i = 0;
                int numRows=0;
                rs1.beforeFirst();
                
                while(rs1.next()){
                    numRows++;
                }
                totalNumberOfRecords = numRows;
                rs1.beforeFirst();
                rs1.absolute(startIndex);
        
                pw.println("<h4> Number of records fetched " + numRows + "\t" + "</h4>");
                pw.println(" <tr bgcolor=\"dd770b\">");
                    
                numberOfPages = totalNumberOfRecords / recordPerPage;
                if (totalNumberOfRecords > numberOfPages * recordPerPage) {
                    numberOfPages = numberOfPages + 1;
                }
                
                pw.println("<caption>");
                for (int k = 1; k <= numberOfPages; k++) {
                    pw.println("<b><a href=Scholarpedia?pageno=" + k + ">" + k + "</a></b>");
                }
                pw.println("</caption>");
                
                if (totalNumberOfRecords>0){
                    pw.println("<table frame=\"box\">");
                    
                    do {
                        i++;
                        pw.println("<tr>");
                        pw.println("<th>PaperTitle :</th>");
                        if(rs1.getString(8).length()!=0)
                            pw.println("<td> <a href=" + rs1.getString(8) + "> "+ rs1.getString(2)+ "</a>");
                        else
                            pw.println("<td width=\"vertical-align:top\"> " + rs1.getString(2) + " " +"," );
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<th>SourceTitle: </th>"); 
                        pw.println("<td width=\"vertical-align:top\"> " + rs1.getString(3) + " " +"," );
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<th>Year: </th>"); 
                        pw.println("<td width=\"vertical-align:top\"> " + rs1.getString(4) + " " +"," );
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<th>Authors: </th>"); 
                        pw.println("<td width=\"vertical-align:top\"> " + rs1.getString(5) + " " +"," );
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<th>Authors with Affiliations: </th>"); 
                        pw.println("<td width=\"vertical-align:top\"> " + rs1.getString(6) + " " +"," );
                        pw.println("</tr>");

                        pw.println("<tr>");
                        pw.println("<th><hr> </th>");
                        pw.println("<td><hr> </td>");
                        pw.println("</tr>");

                    } while (rs1.next() && i != recordPerPage);

                    pw.println("</table>");
                }
                pw.println("</table>");
                
                pw.println("<div align=\"center\">");
                pw.println("<h3>" + "<a href=\"index.html\">New Search</a>" + "</h3>"+ "<br>");
		pw.println("</div>");	
                
                pw.println("</body>");
                pw.println("</html>");
                    
	} 
        catch(SQLException se) {
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        /*finally {
          //finally block used to close resources
         try{
            if(stmt!=null)
               stmt.close();
         }
         catch(SQLException se){
         }// nothing we can do
         
         try{
            if(conn != null)
            conn.close();
         }
         catch(SQLException se){
            se.printStackTrace();
         }
      } *///end try
   }
} 

