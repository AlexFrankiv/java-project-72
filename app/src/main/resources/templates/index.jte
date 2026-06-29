@import hexlet.code.dto.UrlsPage
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="row">
        <div class="col-12">
            <h1>Анализатор страниц</h1>
            <p class="lead">Бесплатно проверяйте сайты на SEO-пригодность</p>

            @if(page.getError() != null)
                <div class="alert alert-danger" role="alert">
                    ${page.getError()}
                </div>
            @endif

            <form action="/urls" method="post">
                <div class="input-group">
                    <input type="url" name="url" class="form-control"
                           placeholder="https://www.example.com"
                           value="${page.getUrl() != null ? page.getUrl() : ""}" required>
                    <button class="btn btn-primary" type="submit">ПРОВЕРИТЬ</button>
                </div>
            </form>
        </div>
    </div>
`
)