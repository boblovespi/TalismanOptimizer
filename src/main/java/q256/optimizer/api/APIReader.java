package q256.optimizer.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import q256.optimizer.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Willi on 5/25/2020.
 */
public class APIReader
{
	public static APIReader API;

	private String apiKey;
	private ExecutorService manager;
	private Gson gson;

	private APIReader(String apiKey)
	{
		this.apiKey = apiKey;
		manager = Executors.newCachedThreadPool();
		gson = new Gson();
	}

	public static void initialize(String apiKey)
	{
		if (apiKey.equals("none"))
			return;
		if (API != null)
			API.kill();
		API = new APIReader(apiKey);
	}

	public void kill()
	{
		manager.shutdown();
	}

	public CompletableFuture<String> nameToUUID(String name)
	{
		CompletableFuture<String> result = new CompletableFuture<>();
		String mojangApi = "https://api.mojang.com/users/profiles/minecraft/" + name;
		manager.submit(() -> {
			try
			{
				URL mojangUrl = new URL(mojangApi);
				BufferedReader in = new BufferedReader(new InputStreamReader(mojangUrl.openStream()));
				String json = in.readLine();
				JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
				if (jsonObject.get("id") != null)
					result.complete(jsonObject.get("id").getAsString());
				else
					result.completeExceptionally(new Exception("id does not exist!"));

			} catch (Exception e)
			{
				e.printStackTrace();
				result.completeExceptionally(e);
			}
		});
		return result;
	}

	private CompletableFuture<JsonObject> get(String request, String... params)
	{
		CompletableFuture<JsonObject> result = new CompletableFuture<>();
		StringBuilder builder = new StringBuilder("https://api.hypixel.net/");
		builder.append(request);
		builder.append("?key=").append(apiKey);
		for (int i = 0; i < params.length / 2; i++)
		{
			builder.append("&").append(params[i * 2]).append("=").append(params[i * 2 + 1]);
		}

		manager.submit(() -> {
			try
			{
				URL mojangUrl = new URL(builder.toString());
				BufferedReader in = new BufferedReader(new InputStreamReader(mojangUrl.openStream()));
				String json = in.readLine();
				result.complete(gson.fromJson(json, JsonObject.class));

			} catch (Exception e)
			{
				e.printStackTrace();
				result.completeExceptionally(e);
			}
		});
		return result;
	}

	public CompletableFuture<JsonObject> profilesData(String uuid)
	{
		return get("skyblock/profiles", "uuid", uuid);
	}

	public CompletableFuture<JsonObject> profilesSkyLea(String username)
	{
		CompletableFuture<JsonObject> result = new CompletableFuture<>();
		StringBuilder builder = new StringBuilder("https://sky.lea.moe/api/v2/profile/");
		builder.append(username);

		manager.submit(() -> {
			try
			{
				URL mojangUrl = new URL(builder.toString());
				URLConnection connection = mojangUrl.openConnection();
				connection.setRequestProperty(
						"User-Agent",
						"q256-optimizer/" + Constants.version + " (https://github.com/boblovespi/TalismanOptimizer)");
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String json = in.readLine();
				result.complete(gson.fromJson(json, JsonObject.class));

			} catch (Exception e)
			{
				e.printStackTrace();
				result.completeExceptionally(e);
			}
		});
		return result;
	}
}
