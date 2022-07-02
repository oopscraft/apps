"use strict";
var duice;
(function (duice) {
    let widget;
    (function (widget) {
        class PaginationFactory extends duice.MapComponentFactory {
            getSelector() {
                return `ul[is="${duice.getAlias()}-widget-pagination"]`;
            }
            getComponent(element) {
                let pagination = new Pagination(element);
                let bind = element.dataset[`${duice.getAlias()}Bind`].split(',');
                pagination.bind(this.getContextProperty(bind[0]), bind[1], bind[2], bind[3]);
                return pagination;
            }
        }
        widget.PaginationFactory = PaginationFactory;
        class Pagination extends duice.MapComponent {
            constructor(ul) {
                super(ul);
                this.lis = new Array();
                this.page = 0;
                this.size = 100;
                this.totalCount = 0;
                this.ul = ul;
                this.addClass(this.ul, 'duice-widget-pagination');
                let li = this.ul.querySelector('li');
                this.li = li.cloneNode(true);
                li.parentNode.removeChild(li);
            }
            bind(map, pageName, rowsName, totalCountName) {
                this.pageName = pageName;
                this.sizeName = rowsName;
                this.totalCountName = totalCountName;
                super.bind(map, pageName);
            }
            setEnable(enable) {
                return;
            }
            update(map, obj) {
                this.page = Number(duice.defaultIfEmpty(map.get(this.pageName), 1));
                this.size = Number(duice.defaultIfEmpty(map.get(this.sizeName), 1));
                this.totalCount = Number(duice.defaultIfEmpty(map.get(this.totalCountName), 0));
                let totalPage = Math.max(Math.ceil(this.totalCount / this.size), 1);
                let startPage = Math.floor((this.page) / 5) * 5;
                let endPage = Math.min(startPage + 5 - 1, totalPage - 1);
                for (let i = this.lis.length - 1; i >= 0; i--) {
                    this.lis[i].parentNode.removeChild(this.lis[i]);
                }
                this.lis.length = 0;
                const prevPage = startPage - 1;
                let prevLi = this.createPageItem(prevPage, '');
                prevLi.style.cursor = 'pointer';
                prevLi.classList.add('duice-widget-pagination__li--prev');
                this.ul.appendChild(prevLi);
                this.lis.push(prevLi);
                if (prevPage < 1) {
                    prevLi.onclick = null;
                    prevLi.style.pointerEvents = 'none';
                    prevLi.style.opacity = '0.5';
                }
                for (let i = startPage; i <= endPage; i++) {
                    const page = i;
                    let li = this.createPageItem(page, String(page + 1));
                    li.style.cursor = 'pointer';
                    this.ul.appendChild(li);
                    this.lis.push(li);
                    if (page === this.page) {
                        li.classList.add('duice-widget-pagination__li--current');
                        li.onclick = null;
                        li.style.pointerEvents = 'none';
                    }
                }
                const nextPage = endPage + 1;
                let nextLi = this.createPageItem(nextPage, '');
                nextLi.style.cursor = 'pointer';
                nextLi.classList.add('duice-widget-pagination__li--next');
                this.ul.appendChild(nextLi);
                this.lis.push(nextLi);
                if (nextPage >= totalPage) {
                    nextLi.onclick = null;
                    nextLi.style.pointerEvents = 'none';
                    nextLi.style.opacity = '0.5';
                }
            }
            getValue() {
                return this.page;
            }
            createPageItem(page, text) {
                let li = this.li.cloneNode(true);
                li.classList.add('duice-widget-pagination__li');
                let $context = {};
                $context['page'] = Number(page);
                $context['text'] = String(text);
                li = this.executeExpression(li, $context);
                li.appendChild(document.createTextNode(text));
                return li;
            }
        }
        widget.Pagination = Pagination;
        duice.addComponentFactory(new PaginationFactory());
    })(widget = duice.widget || (duice.widget = {}));
})(duice || (duice = {}));
