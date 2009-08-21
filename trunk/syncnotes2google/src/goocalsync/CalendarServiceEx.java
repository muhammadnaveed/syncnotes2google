package goocalsync;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gdata.util.RedirectRequiredException;
import com.google.gdata.util.ServiceException;

/*
 その場しのぎ用カレンダーサービス
 */
public class CalendarServiceEx extends
		com.google.gdata.client.calendar.CalendarService {

	public CalendarServiceEx(String applicationName) {
		super(applicationName);
	}

	protected URL handleRedirectException(RedirectRequiredException redirect)
			throws ServiceException {
		String test = redirect.getResponseBody();
		int s = test.indexOf("<A HREF=\"") + 9;
		int f = test.substring(s).indexOf("\">");

		String url = test.substring(s, s + f).replaceAll("&amp;", "&");
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return super.handleRedirectException(redirect);
		}
	}

}
