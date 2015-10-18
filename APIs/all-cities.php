<?php

	require 'inc/connection.inc.php';
	$response_array = array();
	
	$query = "SELECT `id`,`city_name`,`lat`,`lng` FROM `cities` WHERE 1 ORDER BY `city_name` ASC";
	$query_run = mysqli_query($connection, $query);
	
	while($query_row = mysqli_fetch_assoc($query_run)){
		$temp_array = array(
			'id'	=> (int)$query_row['id'],
			'name'	=> ucwords(strtolower($query_row['city_name'])),
			'lat'	=> (float)$query_row['lat'],
			'lng'	=> (float)$query_row['lng']
		);
		
		array_push($response_array, $temp_array);
	}
	
	echo json_encode(array('cities' => $response_array));