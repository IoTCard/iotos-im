package org.jim.server.protocol.http.api;

import org.apache.commons.io.FileUtils;
import org.jim.core.http.HttpRequest;
import org.jim.core.http.HttpResponse;
import org.jim.core.http.UploadFile;
import org.jim.core.packets.User;
import org.jim.server.protocol.http.annotation.RequestPath;
import org.jim.server.util.HttpResps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.json.Json;

import java.io.File;

/**
 * @author WChao
 * 2017年6月29日 下午7:53:59
 */
@RequestPath(value = "/test")
public class TestController extends  IpUtils{
	private static Logger log = LoggerFactory.getLogger(TestController.class);

	/**
	 * @param args
	 * @author WChao
	 */
	public static void main(String[] args) {

	}

	String html = "<div style='position:relation;border-radius:10px;text-align:center;padding:10px;font-size:40pt;font-weight:bold;background-color:##e4eaf4;color:#2d8cf0;border:0px solid #2d8cf0; width:600px;height:400px;margin:auto;box-shadow: 1px 1px 50px #000;position: fixed;top:0;left:0;right:0;bottom:0;'>"
			+ "<a style='text-decoration:none' href='https://git.oschina.net/tywo45/t-io' target='_blank'>"
			+ "<div style='text-shadow: 8px 8px 8px #99e;'>hello tio httpserver</div>" + "</a>" + "</div>";

	String txt = html;

	/**
	 *
	 * @author WChao
	 */
	public TestController() {
	}

	@RequestPath(value = "/abtest")
	public HttpResponse abtest(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.html(request, "OK");
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/bean")
	public HttpResponse bean(User user, HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.json(request, Json.toFormatedJson(user));
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/filetest")
	public HttpResponse filetest(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.file(request, new File("d:/tio.exe"));
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/filetest.zip")
	public HttpResponse filetest_zip(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.file(request, new File("d:/eclipse-jee-neon-R-win32-x86_64.zip"));
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/getsession")
	public HttpResponse getsession(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			String value = (String) request.getHttpSession().getAttribute("test");
			HttpResponse ret = HttpResps.json(request, "获取的值:" + value);
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/html")
	public HttpResponse html(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.html(request, html);
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/json")
	public HttpResponse json(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.json(request, "{\"ret\":\"OK\"}");
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/plain")
	public HttpResponse plain(String before, String end, HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			String bodyString = request.getBodyString();
			HttpResponse ret = HttpResps.html(request, bodyString);
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/post")
	public HttpResponse post(String before, String end, HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.html(request, "before:" + before + "<br>end:" + end);
			return ret;
		}else {
		return null;
		}
	}

	@RequestPath(value = "/putsession")
	public HttpResponse putsession(String value, HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			request.getHttpSession().setAttribute("test", value, request.getHttpConfig());
			HttpResponse ret = HttpResps.json(request, "设置成功:" + value);
			return ret;
		}else {
			return null;
		}
	}

	@RequestPath(value = "/txt")
	public HttpResponse txt(HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret = HttpResps.txt(request, txt);
			return ret;
		}else {
			return null;
		}
	}

	/**
	 * 上传文件测试
	 * @param uploadFile
	 * @param before
	 * @param end
	 * @param request
	 * @return
	 * @throws Exception
	 * @author WChao
	 */
	@RequestPath(value = "/upload")
	public HttpResponse upload(UploadFile uploadFile, String before, String end, HttpRequest request) throws Exception {
		String ipAddr = getIpAddr(request);
		boolean bool =  isWhitelist(ipAddr);
		log.info("ipAddr {} bool {} ",ipAddr,bool);
		if(bool) {
			HttpResponse ret;
			if (uploadFile != null) {
				File file = new File("D:/" + uploadFile.getName());
				FileUtils.writeByteArrayToFile(file, uploadFile.getData());

				System.out.println("【" + before + "】");
				System.out.println("【" + end + "】");

				ret = HttpResps.html(request, "文件【" + uploadFile.getName() + "】【" + uploadFile.getSize() + "字节】上传成功");
			} else {
				ret = HttpResps.html(request, "请选择文件再上传");
			}
			return ret;
		}else {
			return null;
		}
	}
}
