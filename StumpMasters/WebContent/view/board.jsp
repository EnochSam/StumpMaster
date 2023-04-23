<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Enoch a Bigger Poopie</title>
<link rel="stylesheet" href="${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css">
</head>
<body>
    <form method="post" id= "gameForm" action="${pageContext.servletContext.contextPath}/Game">
    <div id="Board">
        
    </div>
    </form>
    <form id ="form" method="get" action="${pageContext.servletContext.contextPath}/MainMenu">
    	<input type="submit" value="Quit Game">
    	<input type = "hidden" value ="" id=possibleMoves >
    </form>
    <script>
    
    	//Grabs Values passed from Servelt and store them in variables
    	let possibleMoves = "${possibleMoves}"
    	let playerTurn = "${playerTurn}";
		let selectingPiece = "${selectingPiece}"
		let selectValidMoves = "${selectValidMoves}"
		let inCheck = "${inCheck}"
		let inCheckmate = "${inCheckmate}"
		let tileStart = ${beginningOfGame};
		let pawnPromotion = "${PawnPromotion}";
		let gameMoves = "${gameMoves}"
		
		//Displays if a player is in Checkmate
				if(inCheckmate == "true"){
			let inCheckSign = document.createElement("div")
			inCheckSign.id = "inCheck"
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
			inCheckSign.id = "inCheck"
			inCheckSign.innerHTML = "Check";
			document.getElementById("gameForm").prepend(inCheckSign)
			
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
            		if("${AddMove}"!="No"){
            		var lastMovex = sessionStorage.getItem("lastMove")[0]
            		var lastMovey = sessionStorage.getItem("lastMove")[2]
            		sessionStorage.setItem("gameMoves",sessionStorage.getItem("gameMoves")+lastMovex+""+lastMovey+" ")
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
            	if(sessionStorage.getItem("lastMove").length == 4){
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
            let board = document.createElement("table");
            board.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
            for(let y=8; y >= 1; y--){
            	//create individual spaces on table
                let tr = document.createElement("tr")
                tr.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
                let td = null;
                let point = null;
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
                    color.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
                    
                    point.onclick = sendPost
                    point.type = "submit"
                    
                   //create imag to store in button
                    pieceimg = document.createElement("img")
                    pieceimg.src = "${pageContext.servletContext.contextPath}/resources/PiecesPNG.png"
                    //Gets String of Piece From Backend 
                    piece =setImageId(x,y)
                    //Picks cropping of Piece from Backend String
             		pieceimg.id = piece;
             		piece.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
             		
                    point.appendChild(pieceimg)
                   //adds button to table row
                    td.appendChild(point)
                    
                    //add table row to table row
                    tr.appendChild(td)
                }
				//
                board.appendChild(tr)

            }
            document.getElementById("Board").appendChild(board)
		
            //checks if Get Values exist
            if(possibleMoves.length > 0){
            	for(let i = 0; i < possibleMoves.length; i+=3){
            		let x = parseInt(possibleMoves[i])
            		console.log(x)
            		let y = parseInt(possibleMoves[(i+1)])
            		let elementName = ""+x+":"+y
            		let shadedArea = document.createElement("shadedArea")
            		shadedArea.style = "${pageContext.servletContext.contextPath}/resources/chessStyleSheet.css"
            		let buttonElement =document.getElementsByName(elementName)[0]
            		buttonElement.prepend(shadedArea)
					
            	}
            }
        
        function setImageId(x, y){
        	if(x == 1 && y == 1){
        		return "${board[0][0]}"
        	}
        	if(x == 1 && y == 2){
        		return "${board[0][1]}"
        	}
        	if(x == 1 && y == 3){
        		return "${board[0][2]}"
        	}
        	if(x == 1 && y == 4){
        		return "${board[0][3]}"
        	}
        	if(x == 1 && y == 5){
        		return "${board[0][4]}"
        	}
        	if(x == 1 && y == 6){
        		return "${board[0][5]}"
        	}
        	if(x == 1 && y == 7){
        		return "${board[0][6]}"
        	}
        	if(x == 1 && y == 8){
        		return "${board[0][7]}"
        	}
        	
        	if(x == 2 && y == 1){
        		return "${board[1][0]}"
        	}
        	if(x == 2 && y == 2){
        		return "${board[1][1]}"
        	}
        	if(x == 2 && y == 3){
        		return "${board[1][2]}"
        	}
        	if(x == 2 && y == 4){
        		return "${board[1][3]}"
        	}
        	if(x == 2 && y == 5){
        		return "${board[1][4]}"
        	}
        	if(x == 2 && y == 6){
        		return "${board[1][5]}"
        	}
        	if(x == 2 && y == 7){
        		return "${board[1][6]}"
        	}
        	if(x == 2 && y == 8){
        		return "${board[1][7]}"
        	}
        	
        	
        
        	if(x == 3 && y == 1){
        		return "${board[2][0]}"
        	}
        	if(x == 3 && y == 2){
        		return "${board[2][1]}"
        	}
        	if(x == 3 && y == 3){
        		return "${board[2][2]}"
        	}
        	if(x == 3 && y == 4){
        		return "${board[2][3]}"
        	}
        	if(x == 3 && y == 5){
        		return "${board[2][4]}"
        	}
        	if(x == 3 && y == 6){
        		return "${board[2][5]}"
        	}
        	if(x == 3 && y == 7){
        		return "${board[2][6]}"
        	}
        	if(x == 3 && y == 8){
        		return "${board[2][7]}"
        	}
        	
        	if(x == 4 && y == 1){
        		return "${board[3][0]}"
        	}
        	if(x == 4 && y == 2){
        		return "${board[3][1]}"
        	}
        	if(x == 4 && y == 3){
        		return "${board[3][2]}"
        	}
        	if(x == 4 && y == 4){
        		return "${board[3][3]}"
        	}
        	if(x == 4 && y == 5){
        		return "${board[3][4]}"
        	}
        	if(x == 4 && y == 6){
        		return "${board[3][5]}"
        	}
        	if(x == 4 && y == 7){
        		return "${board[3][6]}"
        	}
        	if(x == 4 && y == 8){
        		return "${board[3][7]}"
        	}
        	
        	if(x == 5 && y == 1){
        		return "${board[4][0]}"
        	}
        	if(x == 5 && y == 2){
        		return "${board[4][1]}"
        	}
        	if(x == 5 && y == 3){
        		return "${board[4][2]}"
        	}
        	if(x == 5 && y == 4){
        		return "${board[4][3]}"
        	}
        	if(x == 5 && y == 5){
        		return "${board[4][4]}"
        	}
        	if(x == 5 && y == 6){
        		return "${board[4][5]}"
        	}
        	if(x == 5 && y == 7){
        		return "${board[4][6]}"
        	}
        	if(x == 5 && y == 8){
        		return "${board[4][7]}"
        	}
        	
        	
        	if(x == 6 && y == 1){
        		return "${board[5][0]}"
        	}
        	if(x == 6 && y == 2){
        		return "${board[5][1]}"
        	}
        	if(x == 6 && y == 3){
        		return "${board[5][2]}"
        	}
        	if(x == 6 && y == 4){
        		return "${board[5][3]}"
        	}
        	if(x == 6 && y == 5){
        		return "${board[5][4]}"
        	}
        	if(x == 6 && y == 6){
        		return "${board[5][5]}"
        	}
        	if(x == 6 && y == 7){
        		return "${board[5][6]}"
        	}
        	if(x == 6 && y == 8){
        		return "${board[5][7]}"
        	}
        	
        	if(x == 7 && y == 1){
        		return "${board[6][0]}"
        	}
        	if(x == 7 && y == 2){
        		return "${board[6][1]}"
        	}
        	if(x == 7 && y == 3){
        		return "${board[6][2]}"
        	}
        	if(x == 7 && y == 4){
        		return "${board[6][3]}"
        	}
        	if(x == 7 && y == 5){
        		return "${board[6][4]}"
        	}
        	if(x == 7 && y == 6){
        		return "${board[6][5]}"
        	}
        	if(x == 7 && y == 7){
        		return "${board[6][6]}"
        	}
        	if(x == 7 && y == 8){
        		return "${board[6][7]}"
        	}
        	
        	if(x == 8 && y == 1){
        		return "${board[7][0]}"
        	}
        	if(x == 8 && y == 2){
        		return "${board[7][1]}"
        	}
        	if(x == 8 && y == 3){
        		return "${board[7][2]}"
        	}
        	if(x == 8 && y == 4){
        		return "${board[7][3]}"
        	}
        	if(x == 8 && y == 5){
        		return "${board[7][4]}"
        	}
        	if(x == 8 && y == 6){
        		return "${board[7][5]}"
        	}
        	if(x == 8 && y == 7){
        		return "${board[7][6]}"
        	}
        	if(x == 8 && y == 8){
        		return "${board[7][7]}"
        	}
        	
        		
        }
	

		if(pawnPromotion){
			setTimeout( (e)=>{
				let askpp = ""
					while(askpp != "Queen" && askpp != "Knight" && askpp != "Rook" && askpp != "Bishop" ){
						askpp =prompt("What are you promoting your prompt to?")
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