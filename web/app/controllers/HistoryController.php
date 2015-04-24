<?php

class HistoryController extends \BaseController {

	/**
	 * Display a listing of the resource.
	 * GET /history
	 *
	 * @return Response
	 */
	public function getIndex()
	{
		return Response::make('You can try');
	}
    
    public function save($user, $information){
        $history = new History;
        $history->user = $user;
        $history->information = $information;
        $history->save();
    }

}