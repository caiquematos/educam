<?php

class BuddyController extends \BaseController {
    
    private $history;

    public function BuddyController(){
        $this->history = new HistoryController;
    }
	
    public function getIndex()
	{
		return Response::make('Try /invite or /friend or /unfriend');
	}
    
    public function getInvite() {
        $user = User::whereEmail(Input::get('me'))->first();
        $friend = User::whereEmail(Input::get('friend'))->first();
        return $user;
    }
    
    public function getFriend() {
        $user = User::whereEmail(Input::get('me'))->first();
        $friend = User::whereEmail(Input::get('friend'))->first();
        $buddy = Buddy::whereUser($user->id)->whereBuddy($friend->id)->first();
        
        if ( $buddy ) {
            $buddy->status = 'F';
            $buddy->save();
            $result = ["status"=>"success"];
            $this->history->save($user->id, "friended ".$friend->email);
        } else {
            if ( $user and $friend ) {
                $buddy = new Buddy;
                $buddy->user = $user->id;
                $buddy->buddy = $friend->id;
                $buddy->status = 'F';
                $buddy->save();
                $result = ["status"=>"success"];
                $this->history->save($user->id, "got friends with ".$friend->email);
            } else {
                $result = ["status"=>"fail"];
            }
        }
        
        return $result;
    }
    
    public function getUnfriend(){
        $user = User::whereEmail(Input::get('me'))->first();
        $friend = User::whereEmail(Input::get('friend'))->first();        
        $buddy = Buddy::whereUser($user->id)->whereBuddy($friend->id)->whereStatus('F')->first();
        
        if( $buddy ) {
            $buddy->status = 'U';
            $buddy->save();
            $result = ["status"=>"success"];
            $this->history->save($user->id, "unfriended ".$friend->email);
        } else {
            $result = ["status"=>"fail"];
        }
        
        return $result;
    }

}