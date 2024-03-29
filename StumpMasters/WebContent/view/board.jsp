<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>${username}'s Game </title>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css">
</head>
<body>
	
    <form method="post" id= "gameForm" action="${pageContext.servletContext.contextPath}/Game">
        	<input type = "hidden" value ="${username}" name=username >
    
    <div id="Board">
        
    </div>
    <input type="submit" onclick = sendPost() name="Save" value="Save Game">
    	<input type = "hidden" value ="${username}" name=username >
    </form>
    <form id ="form" method="get" action="${pageContext.servletContext.contextPath}/MainMenu">
    	<input type="submit" value="Quit Game">
    	<input type = "hidden" value ="${username}" name=username >
    
    </form>
    <script>
    	let pieceImg = ${boardJson};
    
    	//Grabs Values passed from Servelt and store them in variables
    	let possibleMoves = "${possibleMoves}"
    	let RightCaptured = ${rightCaptured};
    	let LeftCaptured = ${leftCaptured};
    	let playerTurn = "${playerTurn}";
		let selectingPiece = "${selectingPiece}"
		let selectValidMoves = "${selectValidMoves}"
		let inCheck = "${inCheck}"
		let inCheckmate = "${inCheckmate}"
		let tileStart = ${beginningOfGame};
		let pawnPromotion = "${PawnPromotion}";
		let gameMoves = "${gameMoves}"
		let mateinone = "${mateinone}"
		
		//Displays if a player is in Checkmate
			if(inCheckmate == "true"){
			let inCheckSign = document.createElement("div")
			inCheckSign.id = "txt"
			inCheckSign.innerHTML = "Checkmate";
			//Prints Winner
			if(playerTurn == "White"){
				inCheckSign.innerHTML+=" Black Wins!"
			}else{
				inCheckSign.innerHTML+=" White Wins!"

			}
			document.getElementById("gameForm").prepend(inCheckSign)
		}
		//Displays if a player is in Check
		else if(inCheck == "true"){
			let inCheckSign = document.createElement("div")
			inCheckSign.id = "txt"
			inCheckSign.innerHTML = "Check";
			document.getElementById("gameForm").prepend(inCheckSign)
			
		}else if(mateinone == "true"){
			let mateinoneSig = document.createElement("div")
			mateinoneSig.id = "txt"
			mateinoneSig.innerHTML = "Mate Next Move";
			document.getElementById("gameForm").prepend(mateinoneSig)
		}else{
			let mateinoneSig = document.createElement("br")
			mateinoneSig.id = "txt"
			mateinoneSig.innerHTML = "   ";
			document.getElementById("gameForm").prepend(mateinoneSig)
		}
		
    	//Grabs from Servlet the GameMoves Data
    	if(sessionStorage.getItem("gameMoves")!= null && !tileStart){
    		sessionStorage.setItem("gameMoves",gameMoves)
    	}else{
    	sessionStorage.setItem("gameMoves","")
    	sessionStorage.setItem("lastMove","")
    	}
    

        function sendPost(){
            //Creates Tile Submission
            let submit = document.createElement("input")
            submit.name = "tile"
            submit.value = document.activeElement.name
            submit.type = "hidden"
            
            //Switches Attributes 
            if(selectingPiece == "True"){
           		//Player just selected a piece, and next needs to click valid move
           		var locx = parseInt(submit.value[0])
           		var locy = parseInt(submit.value[2])
            	//if the selecting piece has gone from false to true, add lastmove to game moves
            	if(sessionStorage.getItem("lastSelectingPieceState")=="False" && !tileStart){
            		if("${AddMove}"!="No" ){
            		//Checks to make sure that the last move that was made wasn't to save the game
            		if(sessionStorage.getItem("lastMove") !="Save"){
            		var lastMovex = sessionStorage.getItem("lastMove")[0]
            		var lastMovey = sessionStorage.getItem("lastMove")[2]
            		sessionStorage.setItem("gameMoves",sessionStorage.getItem("gameMoves")+lastMovex+""+lastMovey+" ")
            		}
            		}else{
            			var gameMoves = sessionStorage.getItem("gameMoves");
                		gameMoves = gameMoves.substring(0,gameMoves.length -2)
                		sessionStorage.setItem("gameMoves",gameMoves)
            		}
            	}else{
            		if(sessionStorage.getItem("lastSelectingPieceState")=="True" && !tileStart){
            		//If selecetingPieces is true twice in a row, remove the previously added piece, as that is a wrong input
            		var gameMoves = sessionStorage.getItem("gameMoves");
            		gameMoves = gameMoves.substring(0,gameMoves.length -2)
            		sessionStorage.setItem("gameMoves",gameMoves)
            		}
            	}
           		//Pawn Promotion
            	if(sessionStorage.getItem("lastMove").length == 4 && sessionStorage.getItem("lastMove") != "Save"){
            		var pawnPromotedChar = sessionStorage.getItem("lastMove")
            		pawnPromotedChar = pawnPromotedChar[pawnPromotedChar.length-1]
            	 	var gameMovesStr = sessionStorage.getItem("gameMoves")
            		sessionStorage.setItem("gameMoves",gameMovesStr.substring(0,
           					sessionStorage.getItem("gameMoves").length-1)+pawnPromotedChar+" ")
            		
            	}
            	sessionStorage.setItem("gameMoves",sessionStorage.getItem("gameMoves")+locx+""+locy)	

            }
            if(selectValidMoves == "True"){
            	
            }
            sessionStorage.setItem("lastSelectingPieceState",selectingPiece)	
            sessionStorage.setItem("lastMove",submit.value)
            //Create Session Submission
            let Sessionsubmit = document.createElement("input")
            Sessionsubmit.name = "gameMoves"
            Sessionsubmit.value = sessionStorage.getItem("gameMoves")
            Sessionsubmit.type = "hidden"
            
            //Create selectingPiece
            let selectValidMovesSubmission = document.createElement("input")
            selectValidMovesSubmission.name = "selectValidMoves"
            selectValidMovesSubmission.value = selectValidMoves
            selectValidMovesSubmission.type = "hidden"
            
            //Create selectValidMoves
            let selectingPieceSubmission = document.createElement("input")
            selectingPieceSubmission.name = "selectingPiece"
            selectingPieceSubmission.value = selectingPiece
            selectingPieceSubmission.type = "hidden"
            console.log(submit.value)
            
            //Create LastMove Submit
            let LastMovesubmit = document.createElement("input")
            LastMovesubmit.name = "lastMove"
            LastMovesubmit.value = sessionStorage.getItem("lastMove")
            LastMovesubmit.type = "hidden"
            
            //Create PlayerTurn Submit
            let PlayerMovesubmit = document.createElement("input")
            PlayerMovesubmit.name = "playerTurn"
            PlayerMovesubmit.value = playerTurn
            PlayerMovesubmit.type = "hidden"
            
            //Add Submissions to submit
            document.getElementById("Board").appendChild(submit)
            document.getElementById("Board").appendChild(Sessionsubmit)
            document.getElementById("Board").appendChild(selectingPieceSubmission)
            document.getElementById("Board").appendChild(selectValidMovesSubmission)
            document.getElementById("Board").appendChild(LastMovesubmit)
            document.getElementById("Board").appendChild(PlayerMovesubmit)

        }
        
        //Creates board using javascript tags

            //Create table
             //<td> == column
             //<tr> == row
           	let leftRow = document.createElement("table")
           	for(var y = 0; y < 8; y++){
           		let tr = document.createElement("tr");
           		for(var x = 0; x < 2; x++){
           			
           		}
           	 	leftRow.appendChild(tr)
           		
           	}
           	
           	document.getElementById("Board").appendChild(leftRow)
           	
            let board = document.createElement("table");
            for(let y=8; y >= 1; y--){
            	
            	let LeftCaptureColumn = document.createElement("td");
       			let button = document.createElement("div");
       			button.className = "WhiteCaptured"
       			LeftCaptureColumn.style = "width: 50px;height: 50px;"
       			LeftCaptureColumn.appendChild(button)
                
                let LeftCaptureColumn2 = document.createElement("td");
       			let button2 = document.createElement("div");
       			button2.className = "WhiteCaptured"
       			LeftCaptureColumn2.style = "width: 50px;height: 50px;"
       			LeftCaptureColumn2.appendChild(button2)
                
            	//create individual spaces on table
                let tr = document.createElement("tr")

                //tr.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
         		tr.appendChild(LeftCaptureColumn)
         		tr.appendChild(LeftCaptureColumn2)
                 td = null;
                let point = null;
                td = document.createElement("td");
                
                td.innerHTML = y;
                tr.appendChild(td)
                for(let  x= 1; x <= 8; x++){
                    
                    //create new space in x axis
                    td = document.createElement("td");
                    td.oncklick = "submit"
                    td.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
                    
                    //create submit button
                    point = document.createElement("button")
                    point.type = "button"
                    point.value = ""
                    point.name = x+":"+y;
                    if((x+y) %2 == 0){
                        //Black Spaces
                        color = "brown"
                    }else{
                        //White Spaces
                        color = "green"
                    }                 
                    point.id = color;
                    
                    point.onclick = sendPost
                    point.type = "submit"
                    
                   //create imag to store in button
                    pieceimg = document.createElement("img")
                    pieceimg.src = "${pageContext.servletContext.contextPath}/resources/PiecesPNG.png"
                    //Gets String of Piece From Backend 
                    //Picks cropping of Piece from Backend String
             		pieceimg.id = pieceImg["board"][x-1][y-1];             		
                    point.appendChild(pieceimg)
                   //adds button to table row
                    td.appendChild(point)
                    
                    //add table row to table row
                    tr.appendChild(td)
                    
                }
				//
				let RightCaptureColumn = document.createElement("td");
       				button = document.createElement("div");
       				button.className = "BlackCaptured"
       				RightCaptureColumn.appendChild(button)
                	let RightCaptureColumn2 = document.createElement("td");
       				button2 = document.createElement("div");
       				button2.className = "BlackCaptured"
       				RightCaptureColumn2.appendChild(button2)
       				tr.appendChild(RightCaptureColumn)
       				tr.appendChild(RightCaptureColumn2)
                board.appendChild(tr)

            }
          //creates bottom row of letters
          tr =document.createElement("tr");
          td = document.createElement("td");
          br = document.createElement("div");
          
          br.style = "width:130px;"
          td.appendChild(br)
        	  tr.appendChild(td)
            for(let i = 0; i < 8; i++){
				td = document.createElement("td");
                td.style = "width: 50px;"
                br = document.createElement("div");
                br.innerHTML = String.fromCharCode(97+i);
                br.style = "width:50px;"
                td.appendChild(br)
               	tr.appendChild(td)
            }
            board.appendChild(tr)
            document.getElementById("Board").appendChild(board)
			//Fills Captured Rows
			let leftColumns = document.getElementsByClassName("WhiteCaptured")
			for(let i = 0; i < LeftCaptured["leftCaptured"].length; i++){
				img = document.createElement("img")
   				img.src = "${pageContext.servletContext.contextPath}/resources/PiecesPNG.png"
   				img.id = LeftCaptured["leftCaptured"][i]
   				if(img.id == "null"){
   					img.id = "nothing"
   				}
   				leftColumns[i].appendChild(img)
			}
			let rightColumns = document.getElementsByClassName("BlackCaptured")
			for(let i = 0; i < RightCaptured["rightCaptured"].length; i++){
				img = document.createElement("img")
   				img.src = "${pageContext.servletContext.contextPath}/resources/PiecesPNG.png"
   				img.id = RightCaptured["rightCaptured"][i]
				if(img.id == "null"){
   					img.id = "nothing"
   				}
   				rightColumns[i].appendChild(img)
			}
            
           // for(var i = 0;)
            //checks if Get Values exist
            if(possibleMoves.length > 0){
            	for(let i = 0; i < possibleMoves.length; i+=3){
            		let x = parseInt(possibleMoves[i])
            		console.log(x)
            		let y = parseInt(possibleMoves[(i+1)])
            		let elementName = ""+x+":"+y
            		let shadedArea = document.createElement("shadedArea")
            		let buttonElement =document.getElementsByName(elementName)[0]
            		buttonElement.prepend(shadedArea)
					
            	}
            }
	

		if(pawnPromotion){
			setTimeout( (e)=>{
				let askpp = ""
					while(askpp != "Queen" && askpp != "Knight" && askpp != "Rook" && askpp != "Bishop" ){
						askpp =prompt("What are you promoting your pawn to?")
					}
					if(askpp == "Queen"){
						askpp = "Q"
					}
					else
					if(askpp == "Knight"){
						askpp = "K"
					} 
					else
					if(askpp == "Rook"){
						askpp = "R" 
					}else{
						askpp = "B"
					}
					let postPP = document.createElement("input")
					postPP.type = "hidden"
					postPP.name = "pawnPromotion"
					postPP.value = askpp
					
					let newActiveElement = sessionStorage.getItem("lastMove")
					//adds to sessions lastMove
					sessionStorage.setItem("lastMove",sessionStorage.getItem("lastMove")+askpp)
					//grabs Tile element that is existing
					document.getElementById("gameForm").appendChild(postPP)
					document.getElementsByName(newActiveElement)[0].focus()
					document.getElementsByName(newActiveElement)[0].click()
					
					
					
			}, 100)
		}
    </script>
</body>
</html>