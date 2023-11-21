package org.bingmaps.app.sdk;

import androidx.annotation.NonNull;

@SuppressWarnings("MethodDoesntCallSuperMethod")
public class PushpinOptions {
	public int Width;
	public int Height;
	public int ZIndex;
	public String Icon;
	public Point Anchor;
	public String Text;
	public Point TextOffset;
	
	@NonNull
	public PushpinOptions clone(){
		PushpinOptions pOption = new PushpinOptions();
		
		pOption.Width = Width;
		pOption.Height = Height;
		pOption.ZIndex = ZIndex;
		pOption.Icon = Icon;
		pOption.Anchor = Anchor;
		pOption.Text = Text;
		pOption.TextOffset = TextOffset;
		
		return pOption;
	}
	
	@NonNull
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if(Width > 0){
			sb.append(",width:");
			sb.append(Width);
		}
		
		if(Height > 0){	
			sb.append(",height:");
			sb.append(Height);
		}
		
		if(ZIndex > 0){	
			sb.append(",zIndex:");
			sb.append(ZIndex);
		}
		
		if(!Utilities.isNullOrEmpty(Icon)){
			sb.append(",icon:'");
			sb.append(Icon);
			sb.append("'");
		}
		
		if(Anchor != null){
			sb.append(",anchor:");
			sb.append(Anchor.toString());
		}
		
		if(!Utilities.isNullOrEmpty(Text)){
			sb.append(",text:'");
			sb.append(Text);
			sb.append("'");		
		}
		
		if(TextOffset != null){
			sb.append(",textOffset:");
			sb.append(TextOffset.toString());
		}

		sb.append("}");

		if(sb.length() > 2){
			sb.deleteCharAt(1);
		}
		
		return sb.toString();
	}
}
