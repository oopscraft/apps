/// <reference path="../duice.ts" />

namespace duice {

    export namespace widget {

        /**
         * duice.PaginationFactory
         */
        export class PaginationFactory extends MapComponentFactory {
            getSelector(): string {
                return `ul[is="${getAlias()}-widget-pagination"]`;
            }
            getComponent(element:HTMLUListElement):Pagination {
                let pagination = new Pagination(element);
                let bind = element.dataset[`${getAlias()}Bind`].split(',');
                pagination.bind(this.getContextProperty(bind[0]), bind[1], bind[2], bind[3]);
                return pagination;
            }
        }

        /**
         * duice.Pagination
         */
        export class Pagination extends MapComponent {
            ul:HTMLUListElement;
            li:HTMLLIElement;
            lis:Array<HTMLLIElement> = new Array<HTMLLIElement>();
            pageName:string;
            sizeName:string;
            totalCountName:string;
            page:number = 0;
            size:number = 100;
            totalCount:number = 0;
            constructor(ul:HTMLUListElement) {
                super(ul);
                this.ul = ul;
                this.addClass(this.ul, 'duice-widget-pagination');
                
                // clones li
                let li = this.ul.querySelector('li');
                this.li = <HTMLLIElement>li.cloneNode(true);
                li.parentNode.removeChild(li);
            }
            bind(map:Map, pageName:string, rowsName:string, totalCountName:string):void {
                this.pageName = pageName;
                this.sizeName = rowsName;
                this.totalCountName = totalCountName;
                super.bind(map,pageName);
            }
            setEnable(enable:boolean):void {
                return;
            }
            update(map:Map, obj:object):void {
                this.page = Number(defaultIfEmpty(map.get(this.pageName),1));
                this.size = Number(defaultIfEmpty(map.get(this.sizeName),1));
                this.totalCount = Number(defaultIfEmpty(map.get(this.totalCountName),0));

                // defines page
                let totalPage = Math.max(Math.ceil(this.totalCount/this.size),1);
                let startPage = Math.floor((this.page)/5)*5;
                let endPage = Math.min(startPage + 5 -1, totalPage-1);
                
                // clear lis
                for(let i = this.lis.length-1; i >= 0; i --){
                    this.lis[i].parentNode.removeChild(this.lis[i]);
                }
                this.lis.length = 0;
                
                // creates previous item
                const prevPage = startPage - 1;
                let prevLi = this.createPageItem(prevPage,'');
                prevLi.style.cursor = 'pointer';
                prevLi.classList.add('duice-widget-pagination__li--prev');
                this.ul.appendChild(prevLi);
                this.lis.push(prevLi);
                if(prevPage < 1){
                    prevLi.onclick = null;
                    prevLi.style.pointerEvents = 'none';
                    prevLi.style.opacity = '0.5';
                }
                
                // creates page items
                for(let i = startPage; i <= endPage; i ++ ){
                    const page = i;
                    let li = this.createPageItem(page, String(page+1));
                    li.style.cursor = 'pointer';
                    this.ul.appendChild(li);
                    this.lis.push(li);
                    if(page === this.page){
                        li.classList.add('duice-widget-pagination__li--current');
                        li.onclick = null;
                        li.style.pointerEvents = 'none';
                    }
                }
                
                // creates next item
                const nextPage = endPage + 1;
                let nextLi = this.createPageItem(nextPage,'');
                nextLi.style.cursor = 'pointer';
                nextLi.classList.add('duice-widget-pagination__li--next');
                this.ul.appendChild(nextLi);
                this.lis.push(nextLi);
                if(nextPage >= totalPage){
                    nextLi.onclick = null;
                    nextLi.style.pointerEvents = 'none';
                    nextLi.style.opacity = '0.5';
                }
            }
            getValue():any {
                return this.page;
            } 
            createPageItem(page:number, text:string):HTMLLIElement {
                let li:HTMLLIElement = <HTMLLIElement>this.li.cloneNode(true);
                li.classList.add('duice-widget-pagination__li');
                let $context:any = {};
                $context['page'] = Number(page);
                $context['text'] = String(text);
                li = this.executeExpression(li, $context);
                li.appendChild(document.createTextNode(text));
                return li;
            }
        }

        // Adds components
        addComponentFactory(new PaginationFactory());
    }

}