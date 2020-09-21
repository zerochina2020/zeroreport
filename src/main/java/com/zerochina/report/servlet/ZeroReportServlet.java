package com.zerochina.report.servlet;

import com.zerochina.report.domain.Result;
import com.zerochina.report.domain.ZeroReportConfig;
import com.zerochina.report.service.ZeroReportService;
import com.zerochina.report.util.FileUtil;
import com.zerochina.report.util.JsonUtil;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * 报表Servlet
 * @author jiaquan
 * @date 2020/9/10
 */
public class ZeroReportServlet extends HttpServlet {
    private static final String ZERO_REPORT_ROOT = "/";
    private static final String CONFIG_PAGE_PATH = "/config.html";
    private static final String TEMPLATE_PREFIX = "template/";
    private static final String TEMPLATE_DATA_PREFIX = "template/data/";
    private static final String CSS_SUFFIX = ".css";
    private static final String JS_SUFFIX = ".js";
    private static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    private static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8";
    private static final String CONTENT_TYPE_CSS = "text/css;charset=utf-8";
    private static final String CONTENT_TYPE_JS = "text/javascript;charset=utf-8";
    private static final String INTERFACE_CONFIG_PATH = "/config";
    private static final String INTERFACE_CONFIGS_PATH = "/configs";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";

    private final ZeroReportService zeroReportService;

    public ZeroReportServlet(ZeroReportService zeroReportService) {
        this.zeroReportService = zeroReportService;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        accessResource(req, resp);
    }

    private void accessResource(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestURI = trimUrl(req.getRequestURI());
        String contextPath = trimUrl(req.getContextPath());
        String servletPath = trimUrl(req.getServletPath());
        resp.setCharacterEncoding("UTF-8");
        String path = requestURI.substring(contextPath.length() + servletPath.length());

        String filePath;
        if (StringUtils.isEmpty(path) || ZERO_REPORT_ROOT.equals(path)) {
            resp.sendRedirect(contextPath + servletPath + "/config.html");
            return;
        } else if (CONFIG_PAGE_PATH.equals(path)) {
            resp.setContentType(CONTENT_TYPE_HTML);
            filePath = "templates/zeroreport/config.html";
        } else if (path.startsWith(TEMPLATE_DATA_PREFIX) && path.length()>TEMPLATE_DATA_PREFIX.length()) {
            resp.setContentType(CONTENT_TYPE_JSON);
            return;
        } else if (path.startsWith(TEMPLATE_PREFIX) && path.length()>TEMPLATE_PREFIX.length()) {
            resp.setContentType(CONTENT_TYPE_HTML);
            filePath = "templates/zeroreport/template.html";
        } else if (path.equals(INTERFACE_CONFIGS_PATH)) {
            Result result = new Result("SUCCESS", "获取成功");
            try {
                Integer page = Integer.parseInt(req.getParameter("page"));
                Integer limit = Integer.parseInt(req.getParameter("limit"));
                resp.setContentType(CONTENT_TYPE_JSON);
                result = zeroReportService.findZeroReportConfigByPage(page, limit);
            } catch (Exception e) {
                result.setErrorCode("ERROR");
                result.setMessage(e.getMessage());
            }
            resp.setContentType(CONTENT_TYPE_JSON);
            resp.getOutputStream().write(JsonUtil.obj2string(result).getBytes("UTF-8"));
            return;
        } else if (path.equals(INTERFACE_CONFIG_PATH)) {
            Result result = new Result("SUCCESS", "保存成功");
            try {
                ZeroReportConfig reportConfig = JsonUtil.str2obj(getBody(req), ZeroReportConfig.class);
                zeroReportService.saveZeroReportConfig(reportConfig);
            } catch (Exception e) {
                result.setErrorCode("ERROR");
                result.setMessage(e.getMessage());
            }
            resp.setContentType(CONTENT_TYPE_JSON);
            resp.getOutputStream().write(JsonUtil.obj2string(result).getBytes("UTF-8"));
            return;
        } else if (path.endsWith(CSS_SUFFIX)) {
            resp.setContentType(CONTENT_TYPE_CSS);
            filePath = "static/zeroreport" + path;
        } else if (path.endsWith(JS_SUFFIX)) {
            resp.setContentType(CONTENT_TYPE_JS);
            filePath = "static/zeroreport" + path;
        } else {
            filePath = "static/zeroreport" + path;
            byte[] bytes = FileUtil.readByteArrayFromResource(filePath);
            if (bytes != null) {
                resp.getOutputStream().write(bytes);
            } else {
                resp.sendRedirect(contextPath + servletPath + "/config.html");
            }
            return;
        }

        String fileContent = FileUtil.readFromResource(filePath);
        if (StringUtils.isEmpty(fileContent)) {
            resp.sendRedirect(contextPath + servletPath + "/config.html");
            return;
        }
        resp.getWriter().write(fileContent);
    }

    /**
     * 获取HttpServletRequest body内容
     * @param req
     * @return
     * @throws Exception
     */
    private String getBody(HttpServletRequest req) throws Exception {
        StringBuffer data = new StringBuffer();
        String line;
        BufferedReader reader;
        try {
            reader = req.getReader();
            while (null != (line = reader.readLine())){
                data.append(line);
            }
        } catch (IOException e) {
            throw new Exception("获取请求参数异常，异常原因:"+e.getMessage());
        }
        return data.toString();
    }

    /**
     * 去除字符串前后空格
     * @param content
     * @return
     */
    private String trimUrl(String content){
        if(null==content){
            return "";
        }else {
            return content.trim().toLowerCase();
        }
    }
}
