var numRows = 20;
var numCols = 10;
var cellSize = 20;

pieces = []
var pieces_desc = [[3,'111010000001011001000010111100110100'],[2,'1111'],[3,'010010110100111000011010010000111001'],[4,'00001111000000000100010001000100'],[3,'000111100110010010001111000010010011'],[3,'001011010110011000'],[3,'011110000010011001']]
for( p in pieces_desc){
    var size = pieces_desc[p][0]
    var str = pieces_desc[p][1]
    var pieceset = []
    for(i=0,pieceSlice=str.slice(0,size*size);i<str.length;i+=size*size,pieceSlice=str.slice(i,i+size*size)){
        var piece = [];
        for(j=0;j<pieceSlice.length;j+=size){
            rowSlice = pieceSlice.slice(j, j+size);
            var row = []
            for( c in rowSlice ){ row.push( parseInt(rowSlice[c] ) ) }
            piece.push( row )
        }
        pieceset.push( piece )
    } 
    pieces.push(pieceset)
}

        var shape = null;
        var tetrisBoard = {
            setup: function(canvElm){<!--{{{-->
                this.canvElm = canvElm;
                this.board = [[0,0,0,0,0,0,0,0,0,0], 
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0],
                              [0,0,0,0,0,0,0,0,0,0]];
            },<!--}}}-->
            grid: function(){<!--{{{-->
                ctx = this.canvElm.getContext("2d");
                ctx.lineWidth = 1
                ctx.beginPath()
                for( row=0; row<=numRows; row++ ){
                    ctx.moveTo(0, row*cellSize);
                    ctx.lineTo(numCols* cellSize, row*cellSize);
                    ctx.stroke()
                }
                for( col=0; col<=numCols; col++ ){
                    ctx.moveTo(col*cellSize, 0);
                    ctx.lineTo(col*cellSize, numRows*cellSize);
                    ctx.stroke()
                }
            },<!--}}}-->
            draw: function(permanent, doClear){<!--{{{-->
			    ctx = this.canvElm.getContext("2d");
                ctx.clearRect( 0, 0, cellSize*numCols, cellSize*numRows );
                this.grid()
                var newboard = addmatrix( this.board, shape.matrix, shape.x, shape.y )
                if( doClear ){
                    newboard = this.clearlines(newboard);
                }
                for( row=0; row<numRows; row++ ){
                    for( col = 0; col < numCols; col++ ){
                        if( newboard[row][col] > 0 ){
                            ctx.beginPath();
					        ctx.fillRect( col * cellSize, row * cellSize, cellSize, cellSize)
                        }
                    }
                }
                if( permanent ){
                    this.board = newboard
                }
            },<!--}}}-->

            clearlines: function(newboard){
                rowloop: 
                for( row=numRows-1; row>=0; row-- ){
                    var isClear = true;
                    for( col = 0; col < numCols; col++ ){
                        if( newboard[row][col] == 0 ){
                            isClear = false;
                            break;
                        }
                    }
                    if( isClear ){
                        console.debug( "before",newboard.length );
                        newboard.splice(row, 1);
                        console.debug( "after", newboard.length );
                        newboard.unshift([0,0,0,0,0,0,0,0,0,0])
                        console.debug( "afterafter", newboard.length );
                        row++
                    }
                }
                console.debug( newboard.length, numRows )
                //while( newboard.length < numRows ){
                    //newboard.shift([0,0,0,0,0,0,0,0,0,0])
                //}
                return newboard;
            }
        }

        function addmatrix(m1, m2, x, y){<!--{{{-->
            m3 = []
            for(row=0;row<m1.length;row++){ m3[row] = m1[row].slice(0) }
            for( i=0; i<m2.length; i++ ){
                for( j=0; j<m2[i].length; j++){
                    if( y + i < 0 || y + i >= numRows ) continue
                    m3[y + i][x + j] += m2[i][j]
                }
            }
            return m3
        }<!--}}}-->

        function addmatrixIsLegal(m1, m2, x, y){
            for( i=0; i<m2.length; i++ ){
                for( j=0; j<m2[i].length; j++){
                    if( x + j < 0 || x + j >= numCols ){
                        if( m2[i][j] > 0 ) return false
                        else continue
                    }
                    if( y+i < 0 || y + i >= numRows ){
                        if( m2[i][j] > 0 ) return false
                        else continue
                    }
                    console.debug(y, x, i, j, y + i, x + j)
                    if( ( m1[y+i][x+j] + m2[i][j] ) > 1 ) return false
                }
            }
            return true
        }

        function Shape(){<!--{{{-->
            piecenum = Math.floor(Math.random()*7);
            this.pieceset = pieces[piecenum]
            this.pieceset_index = 0;
            this.numrots = this.pieceset.length;
            this.matrix = this.pieceset[ this.pieceset_index ]//init_matrix;
            this.matsize = this.matrix.length;
            var matsize = this.matsize

            this.roomtop = function(){<!--{{{-->
                var count = -1;
                for( i=0;i<matsize;i++ ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
            }<!--}}}-->

            this.x = Math.floor( (numCols - matsize)/2 )
            this.y = ( 0 - this.roomtop())
            //this.y = 0;

            this.rotate = function( grid ){
                var newpieceindex = (this.pieceset_index + 1) % (this.numrots) 
                var newmatrix = this.pieceset[newpieceindex]//init_matrix;  
                if( addmatrixIsLegal( grid, newmatrix, this.x, this.y ) ){
                    this.pieceset_index = newpieceindex
                    this.matrix = newmatrix
                }
            }

            this.canMove = function( grid, dx, dy ){
                return addmatrixIsLegal( grid, this.matrix, this.x + dx, this.y + dy );
            }

            this.roomleft = function(){
                var count = -1;
                for( i=0;i<matsize;i++ ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.roomright = function(){
                var count = -1;
                for( i=matsize-1;i>=0;i-- ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.sideroom = function(right){
                var count = -1;
                for( i=(right?matsize-1:0);(right?i>=0:i<matsize);i+=(right?-1:1) ){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[j][i] > 0 ) return count;
                    }
                }
                return count
            }

            this.clearside = function(right){
                //right = true, left = false
                var incr = right ? -1 : 1;
                var count = -1
                for( i=0;i<matsize;i++){
                    count += 1
                    for( j=right?matsize-1:0;j != (right?-1:matsize);j+=(right?-1:1)){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
                return count;
            }

            this.roombottom = function(){<!--{{{-->
                var count = -1;
                for( i=matsize-1;i>=0;i--){
                    count += 1;
                    for( j=0;j<matsize;j++ ){
                        if( this.matrix[i][j] > 0 ) return count;
                    }
                }
            }<!--}}}-->

            this.bottoms = function(){
                var bottoms = []
                for(i=0;i<this.matsize;i++){
                    for(j=0;j<this.matsize;j++){
                        if(this.matrix[i][j] > 0 && (bottoms[j]==null || bottoms[j] < i)){
                            bottoms[j] = i
                        }
                    }
                }
                return bottoms;
            }

            this.drop = function(grid){
                var bottoms = this.bottoms();
                var maxheight = null;
                for( j=0; j<bottoms.length;j++ ){
                    if( bottoms[j] == undefined ) continue;
                    for(i=this.y+bottoms[j];i<numRows;i++){
                        if( grid[i][this.x + j] > 0 ){
                            var maxheightTest = i + (this.matsize-bottoms[j])-this.matsize-1
                            if( maxheight == null || maxheight> maxheightTest ){
                                maxheight = maxheightTest
                                break;
                            }
                        }
                    }
                }
                if(maxheight==null) maxheight=numRows-this.matsize+this.roombottom()
                return maxheight
            }
        }<!--}}}-->

        function randompiece(){ return pieces[Math.floor(Math.random()*7)][0] }

