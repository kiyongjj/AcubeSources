var client = navigator.appName == 'Microsoft Internet Explorer' ? "IE" : "NE";

var nodes = new Array();

var lastestIdx = 0;

function Tree(tid, rootName){
	this.iconNames = new Array('blank', 
	'vertical_line',
	'closed_lastnode',
	'closed_node',  
	'opened_lastnode', 
	'opened_node',
	'lastnode',
	'node', 
	'closed_default_folder',
	'opened_default_folder');

	this.icons = new Array();
	this.SINGLE_TREE_SELECTION = 0;
	this.CONTIGUOUS_TREE_SELECTION = 1;
	this.DISCONTIGUOUS_TREE_SELECTION = 2;
	this.selectionMode = this.SINGLE_TREE_SELECTION;
	this.selectedNode = new Array();
	this.openedNode = null;
	this.background = "#F5F5F5";
	this.foreground = "#000000";
	this.selectedBackground = "navy";
	this.selectedForeground = "#ffffff";
	this.fontSize = "12";
	this.fontName = "Verdana";
	this.iconDir = "../img";
	this.iconExt = "png";
	this.iconWidth = "19";
	this.iconHeight = "16";
	this.table = document.getElementById(tid);
	this.changedNodes = new Array();
	this.root = new TreeNode(rootName);
	this.loadIcons = function(){
		for(var i = 0; i < this.iconNames.length; i++){
			this.icons[i] = new Image();
			this.icons[i].src = this.iconDir + "/" + this.iconNames[i] + "." + this.iconExt;
		}
		this.BLANK_ICON = this.icons[0];
		this.VERTICAL_LINE_ICON = this.icons[1];
		this.CLOSED_LASTNODE_ICON = this.icons[2];
		this.CLOSED_NODE_ICON = this.icons[3];
		this.OPENED_LASTNODE_ICON = this.icons[4];
		this.OPENED_NODE_ICON = this.icons[5];
		this.LASTNODE_ICON = this.icons[6];
		this.NODE_ICON = this.icons[7];
		this.CLOSED_DEFAULT_FOLDER_ICON = this.icons[8];
		this.OPENED_DEFAULT_FOLDER_ICON = this.icons[9];
	}
	this.loadIcons();
	this.paint = function(){
		if(client == "IE") this.paintForIE();
		else if(client == "NE") this.paintForNE();
	}
	this.paintForIE = function(){
		this.paintNodeForIE(this.root);
	}
	this.paintForNE = function(){
		this.paintNodeForNE(this.root);
	}
	this.getRowIndexById = function(rowId){
		var rows = this.table.childNodes;
		for(var i = 0; i < rows.length; i++){
			if(rows[i].id == rowId)
			return i;
		}
		return - 1;
	}
	this.repaint = function(){
		if(client == "IE"){
         this.repaintForIE();
		}else if(client == "NE"){}
		this.changedNodes = new Array();
	}
	this.repaintAll = function(){
		if(client == "IE"){
			try{
				var size = this.table.rows.length;
				for(var j = 0; j < size; j++){
					this.table.deleteRow(0);
				}
			}catch(exception){
			}
			this.repaintNodeForIE(this.root, 0);
		}else if(client == "NE"){}
		this.changedNodes = new Array();
	}
	this.repaintForIE = function(){
		for(var i = 0; i < this.changedNodes.length; i++){
			var currentNode = this.changedNodes[i];
			 var rowId = "node" + currentNode.index;
			 var nextRowId = - 1;
			 var rowIndex1 = - 1;
			 var rowIndex2 = - 1;
	         if(currentNode != this.root){
				rowIndex1 = this.table.rows[rowId].rowIndex;
				try{
					nextRowId = "node" + currentNode.parent.getChildAfter(currentNode).index;
					rowIndex2 = this.table.rows[nextRowId].rowIndex - 1;
				}catch(exception){
					try{
						nextRowId = "node" + this.getNextOpenedAncestor(currentNode).index;
						rowIndex2 = this.table.rows[nextRowId].rowIndex - 1;
					}catch(exception){
						nextRowId = this.table.rows[this.table.rows.length - 1].id;
						rowIndex2 = this.table.rows[nextRowId].rowIndex;
					}
				}
			}else{
				rowIndex1 = 0;
				rowIndex2 = this.table.rows.length - 1;
			}
			try{
				for(var j = 0; j <(rowIndex2 - rowIndex1) + 1; j++){
					this.table.deleteRow(rowIndex1);
				}
			}catch(exception){
			}
			this.repaintNodeForIE(this.changedNodes[i], rowIndex1);
		}
	}
	this.repaintForNE = function(){
		for(var i = 0; i < this.changedNodes.length; i++){
			var currentNode = this.changedNodes[i];
			 var rowId = "node" + currentNode.index;
			 var nextRowId = - 1;
			 var rowIndex1 = - 1;
			 var rowIndex2 = - 1;
	         if(currentNode != this.root){
				rowIndex1 = this.getRowIndexById(rowId);
				try{
					nextRowId = "node" + currentNode.parent.getChildAfter(currentNode).index;
					rowIndex2 = this.getRowIndexById(nextRowId) - 1;
				}catch(exception){
					alert(exception);
					try{
						nextRowId = "node" + this.getNextOpenedAncestor(currentNode).index;
						rowIndex2 = this.getRowIndexById(nextRowId) - 1;
					}catch(exception){
						nextRowId = this.table.getElementsByTagName("TR").item(this.table.getElementsByTagName("TR").length - 1).id;
						rowIndex2 = this.getRowIndexById(nextRowId);
					}
				}
			}else{
				rowIndex1 = 0;
				rowIndex2 = this.table.getElementsByTagName("TR").length - 1;
			}
			try{
				for(var j = 0; j <(rowIndex2 - rowIndex1) + 1; j++){
					this.table.removeChild(this.table.getElementsByTagName("TR").item(rowIndex1));
				}
			}catch(exception){
				alert(exception);
			}
			this.repaintNodeForNE(this.changedNodes[i], rowIndex1);
		}
	}
	this.paintNodeForIE = function(node){
		var newRow = this.table.insertRow(this.table.rows.length - 1);
		newRow.id = "node" + node.index;
		var newCell = newRow.insertCell();
		var renderString = this.leftSideForIE(node);
		newCell.innerHTML = renderString;
		for(var i = 0; i < node.children.length && node.isCollapsed == 0; i++){
			this.paintNodeForIE(node.children[i]);
		}
	}
	this.paintNodeForNE = function(node){
		var newRow = document.createElement("TR");
		this.table.appendChild(newRow);
		newRow.id = "node" + node.index;
		var newCell = document.createElement("TD");
		newRow.appendChild(newCell);
		newCell.innerHTML = "<!--font size='2'>Mozilla is not Supported. Use IE 5.5+</font-->";
	}
	this.repaintNodeForIE = function(node, index){
		var newRow = this.table.insertRow(index);
		newRow.id = "node" + node.index;
		var newCell = newRow.insertCell();
		var renderString = this.leftSideForIE(node);
		newCell.innerHTML = renderString;
		for(var i = 0; i < node.children.length && node.isCollapsed == 0; i++){
			if(i == 0){
				this.repaintNodeForIE(node.children[i], index + 1);
			}else{
				var rowId = null;
				try{
					rowId = "node" + this.getNextOpenedAncestor(node.children[i - 1]).index;
					this.repaintNodeForIE(node.children[i], this.table.rows[rowId].rowIndex);
				}catch(exception){
					rowId = this.table.rows[this.table.rows.length - 1].id;
					this.repaintNodeForIE(node.children[i], this.table.rows[rowId].rowIndex + 1);
				}
			}
		}
	}
	this.repaintNodeForNE = function(node, index){
		var newRow = document.createElement("TR");
		var indexRow = this.table.getElementsByTagName("TR").item(index);
		//this.appendChild(newRow, index);
		this.table.insertBefore(newRow, indexRow);
		newRow.id = "node" + node.index;
		var newCell = document.createElement("TD");
		newRow.appendChild(newCell);
		var renderString = this.leftSideForNE(node);
		newCell.innerHTML = renderString;
		for(var i = 0; i < node.children.length && node.isCollapsed == 0; i++){
			if(i == 0){
				this.repaintNodeForNE(node.children[i], index + 1);
			}else{
				var rowId = null;
				try{
					rowId = "node" + this.getNextOpenedAncestor(node.children[i - 1]).index;
					this.repaintNodeForNE(node.children[i], this.getRowIndexById(rowId));
				}catch(exception){
		            rowId = this.table.getElementsByTagName("TR").item(this.table.getElementsByTagName("TR").length - 1).id;
					this.repaintNodeForNE(node.children[i], this.getRowIndexById(rowId) + 1);
				}
			}
		}
	}
	this.leftSideForIE = function(node){
		var renderString = "";
		for(var i = 0; i < node.leftside.length; i++){
			var iconSrc = this.BLANK_ICON.src;
			if(node.leftside[i] == 1){
				iconSrc = this.VERTICAL_LINE_ICON.src;
			}
			renderString = renderString + "<IMG border='0' src='" + iconSrc + "' align='absmiddle'>";
		}
		if(node != this.root){
			if(node.isLeaf()){
				var nodeIcon = this.LASTNODE_ICON;
				if(node.parent.getLastChild() != node){
					nodeIcon = this.NODE_ICON;
				}
				renderString = renderString + "<IMG border='0' src='" + nodeIcon.src + "' align='absmiddle'>";
			}else{
				var nodeIcon = null;
				if(node.isCollapsed == 0){
					nodeIcon = this.OPENED_LASTNODE_ICON;
					if(node.parent.getLastChild() != node){
						nodeIcon = this.OPENED_NODE_ICON;
					}
				}else{
					nodeIcon = this.CLOSED_LASTNODE_ICON;
					if(node.parent.getLastChild() != node){
						nodeIcon = this.CLOSED_NODE_ICON;
					}
				}
				renderString = renderString + "<IMG border='0' src='" + nodeIcon.src + "' onClick='nodes[" + node.index + "].tree.toggleNode(nodes[" + node.index + "])' align='absmiddle'>";
			}
		}
		var isSelectedNode = false;
		for(var i = 0; i < this.selectedNode.length; i++){
			if(this.selectedNode[i] == node){
				isSelectedNode = true;
			}
		}
		if(this.openedNode == node){
			var folderIcon = this.OPENED_DEFAULT_FOLDER_ICON;
			if(node.icon != ""){
				folderIcon = new Image();
				folderIcon.src = this.iconDir + "/opened_" + node.icon + "_folder." + this.iconExt;
			}
			renderString = renderString + "<IMG border='0' src='" + folderIcon.src + "' align='absmiddle'>";
		}else{
			var folderIcon = this.CLOSED_DEFAULT_FOLDER_ICON;
			if(node.icon != ""){
				folderIcon = new Image();
				folderIcon.src = this.iconDir + "/closed_" + node.icon + "_folder." + this.iconExt;
			}
			renderString = renderString + "<IMG border='0' src='" + folderIcon.src + "' align='absmiddle'>";
		}
		var fg = this.foreground;
		var bg = this.background;
		if(isSelectedNode){
			fg = this.selectedForeground;
			bg = this.selectedBackground;
		}
		renderString = renderString + "<SPAN style='color:" + fg + ";background-color:" + bg + ";font-size:" + this.fontSize + ";font-family:" + this.fontName + ";cursor:hand'" + " onClick='nodes[" + node.index + "].tree.treeSelectionListener(nodes[" + node.index + "])'>" + node.name + "</SPAN>";
		return renderString;
	}
	this.leftSideForNE = function(node){
		var renderString = "";
		for(var i = 0; i < node.leftside.length; i++){
			var iconSrc = this.BLANK_ICON.src;
			if(node.leftside[i] == 1){
				iconSrc = this.VERTICAL_LINE_ICON.src;
			}
			renderString = renderString + "<IMG border='0' src='" + iconSrc + "' align='absmiddle'>";
		}
		if(node != this.root){
			if(node.isLeaf()){
				var nodeIcon = this.LASTNODE_ICON;
				if(node.parent.getLastChild() != node){
					nodeIcon = this.NODE_ICON;
				}
				renderString = renderString + "<IMG border='0' src='" + nodeIcon.src + "' align='absmiddle'>";
			}else{
				var nodeIcon = null;
				if(node.isCollapsed == 0){
					nodeIcon = this.OPENED_LASTNODE_ICON;
					if(node.parent.getLastChild() != node){
						nodeIcon = this.OPENED_NODE_ICON;
					}
				}else{
					nodeIcon = this.CLOSED_LASTNODE_ICON;
					if(node.parent.getLastChild() != node){
						nodeIcon = this.CLOSED_NODE_ICON;
					}
				}
				renderString = renderString + "<IMG border='0' src='" + nodeIcon.src + "' onClick='nodes[" + node.index + "].tree.toggleNode(nodes[" + node.index + "])' align='absmiddle'>";
			}
		}
		var isSelectedNode = false;
		for(var i = 0; i < this.selectedNode.length; i++){
			if(this.selectedNode[i] == node){
				isSelectedNode = true;
			}
		}
		if(this.openedNode == node){
			var folderIcon = this.OPENED_DEFAULT_FOLDER_ICON;
			if(node.icon != ""){
				folderIcon = new Image();
				folderIcon.src = this.iconDir + "/opened_" + node.icon + "_folder." + this.iconExt;
			}
			renderString = renderString + "<IMG border='0' src='" + folderIcon.src + "' align='absmiddle'>";
		}else{
			var folderIcon = this.CLOSED_DEFAULT_FOLDER_ICON;
			if(node.icon != ""){
				folderIcon = new Image();
				folderIcon.src = this.iconDir + "/closed_" + node.icon + "_folder." + this.iconExt;
			}
			renderString = renderString + "<IMG border='0' src='" + folderIcon.src + "' align='absmiddle'>";
		}
		var fg = this.foreground;
		var bg = this.background;
		if(isSelectedNode){
			fg = this.selectedForeground;
			bg = this.selectedBackground;
		}
		renderString = renderString + "<SPAN style='color:" + fg + ";background-color:" + bg + ";font-size:" + this.fontSize + ";font-family:" + this.fontName + ";cursor:default'" + " onClick='nodes[" + node.index + "].tree.treeSelectionListener(nodes[" + node.index + "])'>" + node.name + "</SPAN>";
		return renderString;
	}
	this.getNextOpenedAncestor = function(node){
      if(client == "IE")
		return this.getNextOpenedAncestorForIE(node);
      else if(client == "NE")
		return this.getNextOpenedAncestorForNE(node);
      else 
		return null;
	}
	this.getNextOpenedAncestorForIE = function(node){
		var rowId = this.table.rows["node" + node.index].rowIndex;
		var nextNode = null;
		for(var i = rowId + 1; i < this.table.rows.length; i++){
			var temp = nodes[this.table.rows[i].id.substring(4, this.table.rows[i].id.length)];
			if(!node.isAncestor(temp)){
				nextNode = temp;
				break;
			}
		}
		return nextNode;
	}
	this.getNextOpenedAncestorForNE = function(node){
		var rowId1 = "node" + node.index;
		var rowId = document.getElementById(rowId1).getAttribute("rowIndex");
		var nextNode = null;
		for(var i = rowId + 1; i < this.table.getElementsByTagName("TR").length; i++){
			var temp = nodes[this.table.getElementsByTagName("TR").item(i).id.substring(4, this.table.getElementsByTagName("TR").item(i).id.length)];
			if(!node.isAncestor(temp)){
				nextNode = temp;
				break;
			}
		}
		return nextNode;
	}
	this.getRowCount = function() {
		if(client == "IE")
			return this.getRowCountForIE();
		else if(client == "NE")
			return this.getRowCountForNE();
		else 
			return null;
	}
	this.getRowCountForIE = function(){
		return this.table.rows.length;
	}
	this.getRowCountForNE = function(){
		return this.table.getElementsByTagName("TR").length;
	}
	this.getSelectionPath = function(){}
	this.moveNode = function(src, target){
		src.parent.remove(src);
		target.add(src);
	}
	this.copyNode = function(src, target){
		var newNode = src.clone();
		target.add(newNode);
	}
	this.isCollapsed = function(index){
		if(client == "IE")
			return this.isCollapsedForIE(index);
		else if(client == "NE")
			return this.isCollapsedForNE(index);
		else 
			return null;
	}
	this.isCollapsedForIE = function(index){
		var id = this.table.rows[index].id;
		id = id.substring(4, id.length);
		return nodes[id].isCollapsed ? true : false;
	}
	this.isCollapsedForNE = function(index){
		var id = this.table.getElementsByTagName("TR").item(index).id;
		id = id.substring(4, id.length);
		return nodes[id].isCollapsed ? true : false;
	}
	this.isExpanded = function(index){
		if(client == "IE")
			return this.isExpandedForIE(index);
		else if(client == "NE")
			return this.isExpandedForNE(index);
		else 
			return null;
	}
	this.isExpandedForIE = function(index){
		var id = this.table.rows[index].id;
		id = id.substring(4, id.length);
		return nodes[id].isCollapsed ? false : true;
	}
	this.isExpandedForNE = function(index){
		var id = this.table.getElementsByTagName("TR").item(index).id;
		id = id.substring(4, id.length);
		return nodes[id].isCollapsed ? false : true;
	}
	this.isSelected = function(index){
		if(client == "IE")
			return this.isSelectedForIE(index);
		else if(client == "NE")
			return this.isSelectedForNE(index);
		else 
			return null;
	}
	this.isSelectedForIE = function(index){
		var id = this.table.rows[index].id;
		id = id.substring(4, id.length);
		return nodes[id].isSelected() ? false : true;
	}
	this.isSelectedForNE = function(index){
		var id = this.table.getElementsByTagName("TR").item(index).id;
		id = id.substring(4, id.length);
		return nodes[id].isSelected() ? false : true;
	}
	this.expandRow = function(index){}
	this.expandPath = function(path){}
	this.expandAll = function(){
		this.root.expandDecendants();
	}
	this.isRowSelected = function(index){
		if(client == "IE")
			return this.isRowSelectedForIE(index);
		else if(client == "NE")
			return this.isRowSelectedForNE(index);
		else 
			return null;
	}
	this.isRowSelectedForIE = function(index){
		var id = this.table.rows[index].id;
		id = id.substring(4, id.length);
		return this.selectedNode == nodes[id] ? true : false;
	}
	this.isRowSelectedForNE = function(index){
		var id = this.table.getElementsByTagName("TR").item(index).id;
		id = id.substring(4, id.length);
		return this.selectedNode == nodes[id] ? true : false;
	}
	this.removeSelectionRow = function(){}
	this.setSelectionRow = function(index){
		if(client == "IE") this.setSelectionRowForIE(index);
		else if(client == "NE") this.setSelectionRowForNE(index);
	}
	this.setSelectionRowForIE = function(index){
		var id = this.table.rows[index].id;
		id = id.substring(4, id.length);
		this.selectNode(nodes[id]);
	}
	this.setSelectionRowForNE = function(index){
		var id = this.table.getElementsByTagName("TR").item(index).id;
		id = id.substring(4, id.length);
		this.selectNode(nodes[id]);
	}
	this.unselectNode = function(node){
		var newArray = new Array();
		for(var i = 0; i < this.selectedNode.length; i++){
			if(this.selectedNode[i] != node) newArray[newArray.length] = this.selectedNode[i];
				else this.addChangedNode(node);
		}
		this.selectedNode = newArray;
	}
	this.selectNode = function(node, event){
		if(this.selectionMode == this.SINGLE_TREE_SELECTION){
			if(this.selectedNode[0] != null){
				this.addChangedNode(this.selectedNode[0]);
			}
			this.selectedNode[0] = node;
			this.openedNode = node;
		}else{
			var withCtrl = false;
			if(client == "IE"){
				try{
					if(event.ctrlKey) withCtrl = true;
				}catch(exceptoin){}
			}else if(client == "NE"){
				try{
					if(event.modifiers == event.CONTROL_MASK) withCtrl = true;
				}catch(exception){
				}
			}
			if(withCtrl){
				if(node.isSelected()){
					this.unselectNode(node);
				}else{
					this.selectedNode[this.selectedNode.length] = node;
				}
			}else{
				for(var i = 0; i < this.selectedNode.length; i++){
					this.addChangedNode(this.selectedNode[i]);
				}
				if(this.openedNode != null) this.addChangedNode(this.openedNode);
				this.selectedNode = new Array();
				this.selectedNode[0] = node;
				this.openedNode = node;
			}
		}
		this.addChangedNode(node);
		this.repaint();
	}
	this.removeFromSelectedNode = function(node){
		var newSelectedNodes = new Array();
		for(var i = 0; i < this.selectedNode.length; i++){
			if(!node == this.selectedNode[i]){
				newSelectedNodes[newSelectedNodes.length] = this.selectedNode[i];
			}
		}
		this.selectedNode = newSelectedNodes;
	}
	this.openNode = function(node){
		if(this.openedNode != null) 
      		this.addChangedNode(this.openedNode);
		this.openedNode = node;
		this.addChangedNode(node);
	}
	this.toggleNode = function(node){
		var newValue = node.isCollapsed == 0 ? 1 : 0;
		node.isCollapsed = newValue;
		if(newValue == 1){
			var setSelected = false;
			for(var i = 0; i < this.selectedNode.length; i++){
				if(node.isAncestor(this.selectedNode[i])){
					this.unselectNode(this.selectedNode[i]);
					setSelected = true;
				}
			}
			if(setSelected) this.selectedNode[this.selectedNode.length] = node;
			if(this.openedNode != null && node.isAncestor(this.openedNode)) this.openNode(node);
		}
		this.addChangedNode(node);
		this.repaint();
	}
	this.addChangedNode = function(node){
		if(!this.hasChangedAncestor(node)) this.changedNodes[this.changedNodes.length] = node;
	}
	this.removeFromChangedNode = function(node){
		var newChangedNodes = new Array();
		for(var i = 0; i < this.changedNodes.length; i++){
			if(!node == this.changedNodes[i]){
				newChangedNodes[newChangedNodes.length] = this.changedNodes[i];
			}
		}
		this.changedNodes = newChangedNodes;
	}
	this.hasChangedAncestor = function(node){
		var tempNode = node;
		var hasChangedAncestor = false;
		while(tempNode != this.root){
			tempNode = tempNode.parent;
			if(this.isChangedNode(tempNode)){
				hasChangedAncestor = true;
				break;
			}
		}
		return hasChangedAncestor;
	}
	this.isChangedNode = function(node){
		var isChanged = false;
		for(var i = 0; i < this.changedNodes.length; i++){
			if(this.changedNodes[i] == node) isChanged = true;
		}
		return isChanged;
	}
	this.treeSelectionListener = function(node){
		this.selectNode(node);
	}
	this.root.tree = this;
	this.root.depth = 0;
	this.root.isCollapsed = 0;
	this.paint();
}
function TreeNode(name){
	this.index = lastestIdx++;
	nodes[this.index] = this;
	this.tree = null;
	this.name = name;
	this.depth = - 1;
	this.leftside = new Array();
	this.isCollapsed = 1;
	this.isAllowsChildren = 0;
	this.icon = "";
	this.parent = null;
	this.children = new Array();
	this.extData = new HashMap();
	this.isLeaf = function(){
		return this.children.length == 0 ? true : false;
	}
	
	this.isAncestor = function(node){
		if(node == this.tree.root)
			return false;
		var parent = node.parent;
		var isAncestor = false;
		while(parent != this.tree.root){
			if(parent == this){
				isAncestor = true;
				break;
			}
			parent = parent.parent;
		}
		return isAncestor;
	}
   
	this.add = function(child){
		this.children[this.children.length] = child;
		child.tree = this.tree;
		child.parent = this;
		child.depth = this.depth + 1;
		if(this.depth > 0){
			child.leftside = new Array();
			for(var i = 0; i < this.leftside.length; i++){
				child.leftside[child.leftside.length] = this.leftside[i];
			}
			child.leftside[child.leftside.length] = this.parent.getLastChild() == this ? 0 : 1;
		}
		child.fixDecendantsInfo();
		if(this.getChildCount() > 1) this.children[this.children.length - 2].fixLeftsideOfChildren();
		if(this.tree != null) this.tree.addChangedNode(this);
	}
	this.expandChildren = function(){
   		this.isCollapsed = 0;
		for(var i=0; i<this.children.length; i++){
			this.children[i].isCollapsed = 0;
		}
	}
	this.expandDecendants = function(){
		this.isCollapsed = 0;
		for(var i=0; i<this.children.length; i++){
			this.children[i].isCollapsed = 0;
			this.children[i].expandDecendants();
		}
	}
	this.fixDecendantsInfo = function(){
		for(var i = 0; i < this.children.length; i++){
			this.children[i].tree = this.tree;
			this.children[i].depth = this.depth + 1;
			this.children[i].leftside = new Array();
			for(var j = 0; j < this.leftside.length; j++){
				this.children[i].leftside[this.children[i].leftside.length] = this.leftside[j];
			}
			this.children[i].leftside[this.children[i].leftside.length] = this.parent.getLastChild() == this ? 0 : 1;
			this.children[i].fixDecendantsInfo();
		}
	}
	this.fixLeftsideOfChildren = function(){
		this.fixLeftsideOfDescendants(this.depth, this.parent.getLastChild() == this ? 0 : 1);
	}
	this.fixLeftsideOfDescendants = function(depth, value){
		for(var i = 0; i < this.children.length; i++){
			this.children[i].leftside[depth - 1] = value;
			this.children[i].fixLeftsideOfDescendants(depth, value);
		}
	}
	this.remove = function(node){
		var newChildren = new Array();
		for(var i = 0; i < this.children.length; i++){
			if(this.children[i] == node){
				node.parent = null;
				node.depth = - 1;
				node.tree = null;
				this.tree.removeFromChangedNode(node);
				this.tree.removeFromSelectedNode(node);
				if(this.tree.openedNode == node){
					this.tree.openedNode = null;
				}
			}else{
				newChildren[newChildren.length] = this.children[i];
			}
		}
		this.children = newChildren;
		if(this.getChildCount() > 0) this.children[this.children.length - 1].fixLeftsideOfChildren();
		if(this.tree != null) this.tree.addChangedNode(this);
	}
	this.removeAllChildren = function(){
		for(var i = 0; i < this.children.length; i++){
			this.children[i].parent = null;
			this.children[i].depth = - 1;
			this.children[i].tree = null;
			this.tree.removeFromChangedNode(this.children[i]);
		}
			this.children = new Array();
			this.tree.addChangedNode(this);
	}
	this.removeFromParent = function(){
		this.parent.remove(this);
	}
	this.sortChild = function(){}
    this.isSelected = function(){
		var isSelected = false;
		if(this.tree != null){
			for(var i = 0; i < this.tree.selectedNode.length; i++){
				if(this.tree.selectedNode[i] == this) isSelected = true;
			}
		}
		return isSelected;
	}
	this.isOpened = function(){
		var isOpened = false;
		if(this.tree != null){
			if(this.tree.openedNode == this) isOpened = true;
		}
		return isOpened;
	}
	this.getChildCount = function(){
		return this.children.length;
	}
	this.getChildAfter = function(node){
		var cAfter = null;
		for(var i = 0; i < this.children.length - 1; i++){
			if(this.children[i] == node) cAfter = this.children[i + 1];
		}
		return cAfter;
	}
	this.getChildBefore = function(node){
		var cBefore = null;
		for(var i = 1; i < this.children.length; i++){
			if(this.children[i] == node) cBefore = this.children[i - 1];
		}
		return cBefore;
	}
	this.getFirstChild = function(){
		return this.children[0];
	}
	this.getLastChild = function(){
		return this.children[this.children.length - 1];
	}
	this.getChildAt = function(index){
		return this.children[index];
	}
	this.getIndex = function(node){
		var index = - 1;
		for(var i = 0; i < this.children.length; i++){
			if(this.children[i] == node) index = i;
		}
		return index;
	}
	this.getPath = function(){}
	this.getRoot = function(){
		return this.tree.root;
	}
	this.put = function(key, value){
		this.extData.put(key, value);
	}
	this.get = function(key){
		var value = null;
		value = this.extData.get(key);
		return value;
	}
	this.clone = function(){
		var newNode = new TreeNode(this.name);
		newNode.icon = this.icon;
		newNode.isAllowChildren = this.isAllowChildren;
		newNode.extData = this.extData;
		for(var i = 0; i < this.getChildCount(); i++){
			newNode.add(this.children[i].clone());
		}
		return newNode;
	}
}

function HashMap(){
	this.keys = new Array();
	this.values = new Array();
	this.clear = function(){}
	this.containsKey = function(key){}
	this.containsValue = function(value){}
	this.isEmpty = function(){}
	this.put = function(key, value){
		this.keys[this.keys.length] = key;
		this.values[this.values.length] = value;
	}
	this.get = function(key){
		var value = null;
		for(var i = 0; i < this.keys.length; i++){
			if(this.keys[i] == key) value = this.values[i];
		}
		return value;
	}
	this.size = function(){}
}

function ListItem(){}

function List(lid){
	var items = new Array();
	this.add = function(item){
		this.items[this.items.length] = item;
	}
}

function TableRow(){
	var columnNames = new Array();
	var columnValues = new Array();
}

function Table(tid){
	var columnNames = new Array();
	var rows = new Array();
}

function FormUtil(){
	this.validate = function(form){}
}
