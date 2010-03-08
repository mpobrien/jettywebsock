var numRows = 20;
var numCols = 10;
var cellSize = 20;

pieces = []
var pieces_desc = [[3,'111010000001011001000010111100110100'],[2,'1111'],[3,'010010110100111000011010010000111001'],[4,'00001111000000000100010001000100'],[3,'000111100110010010001111000010010011'],[3,'001011010110011000'],[3,'011110000010011001']]
for( p in pieces_desc){//{{{
    var size = pieces_desc[p][0]
    var str = pieces_desc[p][1]
    var pieceset = []
    for(i=0,pieceSlice=str.slice(0,size*size);i<str.length;i+=size*size,pieceSlice=str.slice(i,i+size*size)){
        var piece = [];
        for(j=0;j<pieceSlice.length;j+=size){
            rowSlice = pieceSlice.slice(j, j+size);
			var row = 0;
            for( c in rowSlice ){ row |= (parseInt(rowSlice[c]) << c ) }
            piece.push( row )
        }
        pieceset.push( piece )
    } 
    pieces.push(pieceset)
}//}}}

function decode(x){
	var matrix = []
	var rowstrs = x.split(",")
	var i;
	for(i in rowstrs){
		matrix.unshift(parseInt(rowstrs[i]))
	}
	return matrix
}

function prettyShow(mat){
	var i, j;
	var result = "";
	for(i=0;i<mat.length;i++){
		console.debug(mat[i])
		for(j=0;j<10;j++){
			result += (mat[i] & (1<<j)) > 0 ? "1" : "0";
		}
		result += "\n";
	}
	return result;
}

        var shape = null;
        var tetrisBoard = {
            setup: function(canvElm, offset){
				if( !offset ) this.offset = 0;
				else this.offset = offset;
                this.canvElm = canvElm;
				this.board = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0];
            },

			reset: function(board){
				this.board = board
			},

			get: function(row, col){
				return this.board[row] & (1 << col);
			},

			set: function(row, col){
				this.board[row] |= (1 << col);
			},

            grid: function(canvElm){<!--{{{-->
                ctx = canvElm.getContext("2d");
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

			putshape: function(theShape){
				this.board = addmatrix( this.board, theShape.matrix, theShape.x, theShape.y );
			},

            draw: function( canvElm ){<!--{{{-->
			    ctx = canvElm.getContext("2d");
                ctx.clearRect( 0 + this.offset, 0, cellSize*numCols, cellSize*numRows );
                this.grid(canvElm)
                for( row=0; row<numRows; row++ ){
                    for( col = 0; col < numCols; col++ ){
						if( this.get(row, col ) ){
                            ctx.beginPath();
					        ctx.fillRect( col * cellSize + this.offset, row * cellSize, cellSize, cellSize)
                        }
                    }
                }
            },<!--}}}-->

            clearlines: function(){
				var numlines = 0;
                rowloop: 
				for( row = 0; row< numRows; row++){
					if( this.board[row] == 1023 ){
						console.debug("line")
						numlines++;
						this.board.splice(row, 1)
						row--;
					}
				}
				for(i=0;i<numlines;i++){
					this.board.unshift(0)
				}
            }
        }

        function addmatrix(m1, m2, x, y){<!--{{{-->
            m3 = []
            for(row=0;row<m1.length;row++){ m3[row] = m1[row] }
            for( i=0; i < m2.length; i++ ){
                if( x >= 0 ){
				    m3[y+i] |= (m2[i] << x)
                }else{
				    m3[y+i] |= (m2[i] >> (-1*x))
                }
			}
            return m3
        }<!--}}}-->

        function showbinary(x, w){
            result = ''
            for(i=0;i<w;i++){
                result += ( x & (1<<i)) > 0 ? '1' : '0'
            }
            return result
        }


        function addmatrixIsLegal(m1, m2, x, y, w){
            for( i=0; i<m2.length; i++ ){
                if( ( m1[y+i] & (m2[i] << x )) > 0 ){
                    return false;
                }
				if( y+i < 0 || y+i >= numRows ){
					if( m2[i] > 0 ){
                        return false
                    }
				}
                mask =  (1 << (-x)) - 1;
                if( x < 0 ){
                    if( m2[i] & mask > 0 ){
                        return false;
                    }
                }
                if( x + w >= numCols ){
                    if(  (( mask << (numCols - w) ) & m2[i] ) > 0 ) return false
                }
            }
            return true
        }

        function Shape(x){
			var piecenum;
			piecenum = x;
            this.pieceset = pieces[piecenum]
            this.pieceset_index = 0;
            this.numrots = this.pieceset.length;
            this.matrix = this.pieceset[ this.pieceset_index ]//init_matrix;
            this.matsize = this.matrix.length;
            var matsize = this.matsize

			this.get = function(row, col){
				return this.matrix[row] & (1 << col);
			}
			
			this.draw = function(canvElm){
			    ctx = canvElm.getContext("2d");
                for( row=0; row<matsize; row++ ){
                    for( col = 0; col < matsize; col++ ){
						if( this.get(row, col ) ){
                            ctx.beginPath();
					        ctx.fillRect( (this.x + col) * cellSize, (this.y + row) * cellSize, cellSize, cellSize)
                        }
                    }
                }
			}

			this.spaceCount = function(d){//{{{
				var count = 0, incr = -1, startIndex = matsize - 1, stopIndex = -1;
				if( d == 't' || d == 'b' ){
					for( i =  (d=='t') ? 0 : matsize-1; i != (d=='t' ? matsize : - 1); i += d=='t' ? 1 : -1){
						if( this.matrix[i] == 0 ) count++; 
						else break;
					}
					return count;
				}else{
					var j;
					for( j = ( d=='l' ? 0 : matsize-1 ); j != ( d=='l' ? matsize : -1 ); j += ( d=='l' ? 1 : -1 ) ){
						for( i=0; i < matsize; i++ ){
							if( this.get(i,j) > 0 ) return count;
						}
						count++;
					}
					return count;
				}
			}//}}}

            this.x = Math.floor( (numCols - matsize)/2 )
            this.y = ( 0 - this.spaceCount('t'))

            this.rotate = function( grid ){
                var newpieceindex = (this.pieceset_index + 1) % (this.numrots) 
                var newmatrix = this.pieceset[newpieceindex]//init_matrix;  
                if( addmatrixIsLegal( grid, newmatrix, this.x, this.y, this.matsize ) ){
                    this.pieceset_index = newpieceindex
                    this.matrix = newmatrix
                }
            }

            this.canMove = function( grid, dx, dy ){
                return addmatrixIsLegal( grid, this.matrix, this.x + dx, this.y + dy, this.matsize );
            }

            this.bottoms = function(){
                var bottoms = []
                for(i=0;i<this.matsize;i++){
                    for(j=0;j<this.matsize;j++){
                        if(this.get(i,j) > 0 && (bottoms[j]==null || bottoms[j] < i)){
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
						if(grid.get(i, this.x + j) > 0 ) {
                            var maxheightTest = i + (this.matsize-bottoms[j])-this.matsize-1
                            if( maxheight == null || maxheight> maxheightTest ){
                                maxheight = maxheightTest
                                break;
                            }
                        }
                    }
                }
                if(maxheight==null) maxheight=numRows-this.matsize + this.spaceCount('b')
                return maxheight
            }
        }

        function randompiece(){ return pieces[Math.floor(Math.random()*7)][0] }
