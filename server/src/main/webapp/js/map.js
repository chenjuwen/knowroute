/**
 * 创建和初始化Map对象
 * @param containerId
 * @param point
 * @returns Map
 */
function newMap(_containerId, _point, _showContextMenu){
	var map = new BMap.Map(_containerId); //创建地图实例
	map.centerAndZoom(_point, 18); //初始化地图，设置中心点坐标和地图级别
	//map.centerAndZoom("罗定", 15);		//根据城市名定位
	map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
	map.enableContinuousZoom(); //启用地图惯性拖拽
	
	//缩放控件
	var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_LEFT, type: BMAP_NAVIGATION_CONTROL_SMALL})
	map.addControl(top_right_navigation);
	
	map.addControl(new BMap.ScaleControl());	//比例尺
	
	if(_showContextMenu){
	    var menu = new BMap.ContextMenu();
		var txtMenuItem = [
			{
				text:'清除所有覆盖物',
				callback:function(){
					map.clearOverlays();
				}
			}
		];
		for(var i=0; i < txtMenuItem.length; i++){
			menu.addItem(new BMap.MenuItem(txtMenuItem[i].text, txtMenuItem[i].callback, 100));
		}
		map.addContextMenu(menu);
	}
	
	return map;
}

/**
 * 创建覆盖物
 * @param _map
 * @param _point
 * @param _clickFunction
 * @param _lable
 * @param _isAnimation
 */
function addMarker(_map, _point, _clickFunction, _lable, _isAnimation, _showContextMenu){
	var marker = new BMap.Marker(_point);
	_map.addOverlay(marker); //添加覆盖物
	//_map.panTo(_point); //移动地图

	if(_clickFunction != null){
		marker.addEventListener('click', _clickFunction);
	}

	if(_lable != null){
		marker.setLabel(_lable);
	}

	if(_isAnimation){
		marker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	}
	
	//右键菜单
	if(_showContextMenu){
		var removeMarker = function(e, ee, marker){
			_map.removeOverlay(marker);
		}
		var markerMenu = new BMap.ContextMenu();
		markerMenu.addItem(new BMap.MenuItem('删除', removeMarker.bind(marker)));
		marker.addContextMenu(markerMenu);
	}
	
	return marker;
}

