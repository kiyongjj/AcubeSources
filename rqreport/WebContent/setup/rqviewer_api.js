function IE_Viewer_Change(width,height)
{
	document.write('<object id="RQViewer" classid="clsid:94542C25-D867-4C5D-BD5C-FFAFD9916C0B" width="' + width + '" height="' + height + '">'); 
	document.write('<param name="ToolBarVisible" value="true">');
	document.write('<param name="ShowProgressDialog" value="true">');
	document.write('<param name=SPBarVisible value=true>');
	document.write('<param name="BackColor" value="16777215">');
	document.write('</object>');
}
