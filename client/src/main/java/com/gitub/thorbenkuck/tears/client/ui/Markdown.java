package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Notes;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.controlsfx.control.PopOver;

import java.util.Arrays;
import java.util.List;

public class Markdown {

	private static final Parser parser;
	static {
		List<Extension> extensions = Arrays.asList(TablesExtension.create(), StrikethroughExtension.create());
		parser = Parser.builder()
				.extensions(extensions)
				.build();

		renderer = HtmlRenderer.builder()
				.extensions(extensions)
				.build();
	}
	private static final HtmlRenderer renderer;

	public static WebView toWebView(String text) {
		WebView webView = new WebView();
//		webView.getEngine().setUserStyleSheetLocation(Markdown.class.getClassLoader().getResource("styles/bootstrap.css").toString());

		if(text.toLowerCase().startsWith("#!no_markdown")) {
			webView.getEngine().loadContent(text.replaceAll("#!no_markdown", ""));
			return webView;
		}

		Node document = parser.parse(text);
		String content = renderer.render(document);
		webView.getEngine().loadContent(content);

		HBox.setHgrow(webView, Priority.ALWAYS);
		VBox.setVgrow(webView, Priority.ALWAYS);

		return webView;
	}

	public static WebView toWebView(Notes notes) {
		return toWebView(notes.getContent());
	}

}
