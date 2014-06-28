package com.barchart.ondemand;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.barchart.ondemand.api.BalanceSheetsRequest;
import com.barchart.ondemand.api.CompetitorsRequest;
import com.barchart.ondemand.api.CorporateActionsRequest;
import com.barchart.ondemand.api.FinancialHighlightRequest;
import com.barchart.ondemand.api.FuturesOptionsRequest;
import com.barchart.ondemand.api.IncomeStatementRequest;
import com.barchart.ondemand.api.IndexMembersRequest;
import com.barchart.ondemand.api.OnDemandRequest;
import com.barchart.ondemand.api.ProfileRequest;
import com.barchart.ondemand.api.QuoteRequest;
import com.barchart.ondemand.api.RatingsRequest;
import com.barchart.ondemand.api.SDFuturesOptionsRequest;
import com.barchart.ondemand.api.SignalsRequest;
import com.barchart.ondemand.api.TechnicalsRequest;
import com.barchart.ondemand.api.responses.BalanceSheets;
import com.barchart.ondemand.api.responses.Competitors;
import com.barchart.ondemand.api.responses.CorporateActions;
import com.barchart.ondemand.api.responses.FinancialHighlights;
import com.barchart.ondemand.api.responses.FuturesOptions;
import com.barchart.ondemand.api.responses.IncomeStatements;
import com.barchart.ondemand.api.responses.IndexMembers;
import com.barchart.ondemand.api.responses.Profiles;
import com.barchart.ondemand.api.responses.Quotes;
import com.barchart.ondemand.api.responses.Ratings;
import com.barchart.ondemand.api.responses.ResponseBase;
import com.barchart.ondemand.api.responses.SDFuturesOptions;
import com.barchart.ondemand.api.responses.Signals;
import com.barchart.ondemand.api.responses.Technicals;
import com.barchart.ondemand.util.JsonUtil;
import com.barchart.ondemand.util.QueryUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Java client for Barchart's OnDemand APIs.
 * 
 * 
 * <pre>
 * BarchartOnDemandClient client = new BarchartOnDemandClient.Builder().apiKey(&quot;API-KEY&quot;).build();
 * </pre>
 * 
 * @see <a href="http://barchartondemand.com">http://barchartondemand.com</a>
 * 
 * @author m-ehrenberg
 * 
 * 
 */

public class BarchartOnDemandClient {

	private final String apiKey;
	private final String baseUrl;
	private final long cacheTime;

	private final OkHttpClient http = new OkHttpClient();

	private final Map<Class<?>, Class<?>> responseMap = new HashMap<Class<?>, Class<?>>();

	//

	private BarchartOnDemandClient(final Builder b) {
		this.apiKey = b.apiKey;
		this.baseUrl = b.baseUrl;
		this.cacheTime = b.cacheTime;

		responseMap.put(QuoteRequest.class, Quotes.class);
		responseMap.put(TechnicalsRequest.class, Technicals.class);
		responseMap.put(SignalsRequest.class, Signals.class);
		responseMap.put(BalanceSheetsRequest.class, BalanceSheets.class);
		responseMap.put(CompetitorsRequest.class, Competitors.class);
		responseMap.put(CorporateActionsRequest.class, CorporateActions.class);
		responseMap.put(FinancialHighlightRequest.class, FinancialHighlights.class);
		responseMap.put(IncomeStatementRequest.class, IncomeStatements.class);
		responseMap.put(IndexMembersRequest.class, IndexMembers.class);
		responseMap.put(ProfileRequest.class, Profiles.class);
		responseMap.put(RatingsRequest.class, Ratings.class);
		responseMap.put(FuturesOptionsRequest.class, FuturesOptions.class);
		responseMap.put(SDFuturesOptionsRequest.class, SDFuturesOptions.class);

	}

	//

	public <T extends ResponseBase> ResponseBase fetch(final OnDemandRequest request) throws Exception {

		if (request == null) {
			throw new RuntimeException("request cannot be null.");
		}

		final StringBuilder sb = new StringBuilder();

		sb.append(baseUrl);
		sb.append(request.endpoint());
		sb.append("?");
		sb.append(QueryUtil.urlEncodeUTF8(request.parameters()));
		sb.append("&apikey=");
		sb.append(apiKey);

		final String response = fetchString(sb.toString(), http);

		return (ResponseBase) JsonUtil.fromJson(responseMap.get(request.getClass()), response);

	}

	private String fetchString(final String url, final OkHttpClient client) throws IOException {

		final Request request = new Request.Builder().url(url).build();
		final Response response = client.newCall(request).execute();
		final String body = response.body().string();

		if (QueryUtil.isResponseError(body)) {
			throw new RuntimeException("API call has failed: " + body);
		}

		return body;
	}

	//

	public static class Builder {

		private String baseUrl = "http://ondemand.websol.barchart.com/";
		private String apiKey;
		private long cacheTime = 10000;

		public Builder baseUrl(final String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public Builder apiKey(final String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder cacheTime(final long cacheTime) {
			this.cacheTime = Math.max(3000, cacheTime);
			return this;
		}

		public BarchartOnDemandClient build() {

			if (StringUtils.isBlank(apiKey)) {
				throw new UnsupportedOperationException("API key cannot be null.");
			}

			return new BarchartOnDemandClient(this);
		}
	}

}
