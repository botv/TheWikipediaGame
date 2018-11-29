import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		findPath("https://en.wikipedia.org/wiki/Squirrel", "https://en.wikipedia.org/wiki/Scrotum", 0, 3, new ArrayList<>());
	}

	private static String getHTML(String url) {
		String content = null;
		URLConnection connection;

		try {
			connection = new URL(url).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return content;
	}

	private static ArrayList<String> getLinks(String url) {
		// get page HTML
		String html = getHTML(url);

		// define final links array
		ArrayList<String> links = new ArrayList<>();

		// define indexes array and initialize original index
		int linkIndex = html.indexOf("href=\"/wiki/");

		// get all indexes of links in the html
		while (linkIndex >= 0) {
			// get link
			String link = html.substring(linkIndex + 6, html.indexOf("\"", linkIndex + 6));

			// check if the link points to an article
			if (!(link.contains(":") || link.contains("("))) {
				// add link to array and update link element index
				links.add(link);
			}

			linkIndex = html.indexOf("href=\"/wiki/", linkIndex + 1);
		}

		// remove duplicates
		links = new ArrayList<>(new LinkedHashSet<String>(links));

		// add beginning of URL
		for (int i = 0; i < links.size(); i++) {
			links.set(i, "https://en.wikipedia.org" + links.get(i));
		}

		return links;
	}

	private static void findPath(String startURL, String endURL, int depth, int maxDepth, ArrayList<String> pathSoFar) {
		if (depth > maxDepth) {
			return;
		}

		// breadth first
		for (String link : getLinks(startURL)) {
			if (link.equals(endURL)) {
				pathSoFar.add(link);
				System.out.println(pathSoFar);
				return;
			}
		}

		// then recurse down tree
		for (String link : getLinks(startURL)) {
			if (pathSoFar.indexOf(link) == -1) {
				pathSoFar.add(link);
				findPath(link, endURL, depth + 1, maxDepth, pathSoFar);
				pathSoFar.remove(link);
			}
		}
	}
}
