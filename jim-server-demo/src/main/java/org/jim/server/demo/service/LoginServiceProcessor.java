/**
 *
 */
package org.jim.server.demo.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jim.core.*;
import org.jim.core.packets.*;
import org.jim.core.session.id.impl.UUIDSessionIdGenerator;
import org.jim.server.processor.login.LoginCmdProcessor;
import org.jim.server.protocol.AbstractProtocolCmdProcessor;
import org.jim.server.util.iotos.AesEncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author iotos
 *
 */
public class LoginServiceProcessor extends AbstractProtocolCmdProcessor implements LoginCmdProcessor {

	private Logger logger = LoggerFactory.getLogger(LoginServiceProcessor.class);

	public static final Map<String, User> tokenMap = new HashMap<>();

	public static final Map<String, String> jsonMap = new HashMap<>();


	// 后端 服务地址
	public static final String iotosIp ="http://127.0.0.1:8080";


	/**
	 * 根据用户名和密码获取用户
	 * @param loginReqBody
	 * @param imChannelContext
	 * @return
	 * @author: WChao
	 */
	public User getUser(LoginReqBody loginReqBody, ImChannelContext imChannelContext) {
		/*String text = loginReqBody.getUserId()+loginReqBody.getPassword();
		String key = ImConst.AUTH_KEY;
		String token = Md5.sign(text, key, CHARSET);*/

		String token = loginReqBody.getPassword();
		User user = getUser(token);
		user.setUserId(loginReqBody.getUserId());
		return user;
	}
	/**
	 * 根据token获取用户信息
	 * @param token
	 * @return
	 * @author: WChao
	 */
	public User getUser(String token) {
		//demo中用map，生产环境需要用cache [Redis]
		User user = tokenMap.get(token);
		if(Objects.nonNull(user)){
			return user;
		}

		//这里检查 用户需要加入那些群组    [默认加入 系统广播群组 和 企业把内部群组]

		String tKey =  "iotos_im_"+token;
		JSONObject jsonUser = JSONObject.parseObject(jsonMap.get(tKey));//获取登录验证时的 iotos user 信息
		String userName = jsonUser.get("userName").toString();//登录名
		String nickName = jsonUser.get("nickName").toString();//用户昵称
		JSONObject dept = (JSONObject) jsonUser.get("dept");//企业信息
		String deptId = dept.get("deptId").toString();//企业ID
		String deptName = dept.get("deptName").toString();//企业名称
		String avatar = dept.get("avatar")!=null?dept.get("avatar").toString():"";//用户头像


		User.Builder builder = User.newBuilder().userId(userName).nick(nickName)
				.addGroup(Group.newBuilder().groupId(deptId).name(deptName).build());
		//加载群组好友;
		initFriends(builder);
		builder.avatar(avatar).status(UserStatusType.ONLINE.getStatus());
		user = builder.build();
		if (tokenMap.size() > 10000) {
			tokenMap.clear();
			jsonMap.clear();
		}
		tokenMap.put(token, user);
		return user;
	}

	/**
	 * 初始化我的好友
	 * @param builder
	 */
	public void initFriends(User.Builder builder){
		Group myFriend = Group.newBuilder().groupId("1").name("我的好友").build();
		List<User> myFriendGroupUsers = new ArrayList();
		//后续冲数据库读取好友列表（待完善）
		User user1 = User.newBuilder()
				.userId(UUIDSessionIdGenerator.instance.sessionId(null))
				.nick("XXX")
				.avatar(nextImg())
				.build();


		myFriendGroupUsers.add(user1);
		myFriend.setUsers(myFriendGroupUsers);
		builder.addFriend(myFriend);
	}

	public String nextImg() {
		String imgUrl = ImgMnService.nextImg();
		return imgUrl;
	}

	/**
	 * 登录成功返回状态码:ImStatus.C10007
	 * 登录失败返回状态码:ImStatus.C10008
	 * 注意：只要返回非成功状态码(ImStatus.C10007),其他状态码均为失败,此时用户可以自定义返回状态码，定义返回前端失败信息
	 */
	@Override
	public LoginRespBody doLogin(LoginReqBody loginReqBody, ImChannelContext imChannelContext) {
		//这里是 用户账号密码 登录判断逻辑处理部分

		//1.加密请求到 iotos 校验 是否登录 （请求是否成功）
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("token",loginReqBody.getPassword());
		String urlString = iotosIp+"/prod-api/iotos/im/imLogin";
		try {
			String pwdStr = AesEncryptUtil.encrypt(JSON.toJSONString(paramMap));
			String rStr = HttpUtil.post(urlString, pwdStr);
			System.out.println(rStr);
			JSONObject jsonData = AesEncryptUtil.getParameter(rStr);
			System.out.println("jsonData "+jsonData);
			String code = jsonData.getString("code");
			if(code.equals("200")){
				JSONObject user = jsonData.getJSONObject("data");
				//System.out.println(user);
				//将基础用户信息存入到 redis 中 供其他组件部分获取数据
				String tKey =  "iotos_im_"+paramMap.get("token").toString();
				jsonMap.put(tKey, JSON.toJSONString(user));
				if(user!=null){
					return LoginRespBody.success();
				}
			}
		}catch (Exception e){
			logger.error("加密请求到 iotos 校验 异常 "+e.getMessage());
		}
		return LoginRespBody.failed();
	}

	@Override
	public void onSuccess(User user, ImChannelContext channelContext) {
		logger.info("登录成功回调方法");
	}

	@Override
	public void onFailed(ImChannelContext imChannelContext) {
		logger.info("登录失败回调方法");
	}
}
