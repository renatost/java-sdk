package com.saferize.sdk;

import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saferize.sdk.internals.SaferizeConnection;
import com.saferize.sdk.internals.WebsocketClient;
import com.saferize.sdk.internals.WebsocketConnection;

public final class SaferizeClient implements WebsocketConnection {
	
	private SaferizeConnection connection;
	private WebsocketClient websocket;
	private Gson gson;
	private SaferizeSession session;
	private Consumer<SaferizeSession> onPaused;
	
	
	public  SaferizeClient(Configuration configuration) throws AuthenticationException, WebsocketException {
		this.connection = new SaferizeConnection(configuration);
		this.websocket = new WebsocketClient(configuration);
		this.gson = new Gson();
	}
		
	
	public Approval signUp(String parentEmail, String userToken) throws ConnectionException {
		JsonObject root = new JsonObject();
		JsonObject user = new JsonObject();
		JsonObject parent = new JsonObject();
		root.add("user", user);
		root.add("parent", parent);
		
		user.addProperty("token", userToken);
		parent.addProperty("email", parentEmail);
		
		String resp = connection.post("/approval", root.toString());
		Approval approval = gson.fromJson(resp, Approval.class);
		return approval;
	}
	
	public SaferizeSession createSession(String userToken) throws ConnectionException {		
		String resp = connection.post("/session/app/" + userToken, "");
		session = gson.fromJson(resp, SaferizeSession.class);
		return session;
	}
	
	public void startWebsocketConnection() throws WebsocketException {
		websocket.subscribe(this);
		if (session == null) {
			throw new WebsocketException("Cannot start websocket connection before initiating a session");
		}
		websocket.connect(session);		
	}

	public void onPause(Consumer<SaferizeSession> onPause) {
		this.onPaused = onPause;
	}
	

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onMessage(String message) {		
		if (onPaused != null) {
			onPaused.accept(null);
		}
	}


	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}
	

}