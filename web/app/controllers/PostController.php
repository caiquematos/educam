<?php

class PostController extends \BaseController {

	/**
	 * Display a listing of the resource.
	 * GET /post
	 *
	 * @return Response
	 */
	public function getIndex()
	{
	   return Response::make('You can try /post or /retreive or /edit or /remove or /all');	
	}
    
    public function anyPost() {
        $json = json_decode(Input::get("json"));
        $user = User::find($json->user);
        if( $user ) {
            $post = new Post;
            $post->user = $user->id;
            //$post->photo = $json->photo;
            $post->title = $json->title;
            $post->location = $json->location;
            $post->likes = $json->likes;
            $post->save();
            
            $base=str_replace(" ", "+", $json->photo);
            $decoded = base64_decode($base);
            header('Content-Type: bitmap; charset=utf-8');
            $file = fopen(public_path().'/uploads/'.$post->id.'.jpg', 'wb');
            fwrite($file, $decoded);
            fclose($file);  
            
            $result = ["status"=>"success"];
        } else {        
            $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
    
    public function anyPhoto() {
        
        if ( Input::hasFile("photo") && Input::file("photo")->isValid() )
        {
          $fileName = "/uploads/" . Input::get("id") . "_" . microtime(true) . ".jpg";

          switch(Input::file("photo")->getMimeType())
          {
            case "image/png":
            case "image/jpeg":
            case "image/gif":
              break;
            default:
              return ["status" => "fail"];
          }

          $image    = new Imagick(Input::file("photo")->getRealPath());
          $width   = $image->getImageWidth();
          $height  = $image->getImageHeight();
          if ( $width < $height )
            $image->cropImage( $width, $width, 0, ($height-$width)/2);
          else
            $image->cropImage( $height, $height, ($width-$height)/2, 0);

          if ( $image->getImageHeight() > 400)
            $image->thumbnailImage(400, 400);

          //~ Input::file("imageproduct")->move("uploads", $fileName);
          $image->writeImage(__DIR__ . "/../../public" . $fileName);

          return Post::whereId(Input::get("id"))->update(["photo" => $fileName ]) ?
                                ["status" => "success"] :
                                ["status" => "fail"];
        }
    else
      return ["status" => "fail"];
    }
    
    public function anyRetreive() {
        $json = json_decode(Input::get("json"));
        $post = Post::find($json->id);
        if( $post ) {
            $result = ["status"=>"success", "post"=>$post];
        } else {
            $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
    
    public function anyEdit() {
        $json = json_decode(Input::get("json"));
        $post = Post::find($json->id);
        if( $post ) {
            $post->photo = $json->photo;
            $post->title = $json->title;
            $post->location = $json->location;
            $post->likes = $json->likes;
            $post->save();
            $result = ["status"=>"success", "post"=>$post];
        } else {
            $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
    
    public function anyRemove() {
        $json = json_decode(Input::get("json"));
        $post = Post::find($json->id);
        if( $post ) {
            $post->delete();
            $result = ["status"=>"success"];
        } else {
            $result = ["status"=>"fail"];
        }
        
        return Response::json($result);
    }
    
    public function anyAll() {
        $posts = Post::orderBy("id", "DESC")->get();
        foreach ($posts as $post) {
            $user = User::find($post->user);    
            $post->email = $user->email;
        }
        $result = ["status"=>"success", "posts"=>$posts];
        return Response::json($result);
    }

}