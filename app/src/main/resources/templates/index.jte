@template.layout.page(
content = @`
    <div class="row">
        <div class="col-12">
            <h1>Анализатор страниц</h1>
            <p class="lead">Бесплатно проверяйте сайты на SEO-пригодность</p>
            <form action="/urls" method="post">
                <div class="input-group">
                    <input type="url" name="url" class="form-control" placeholder="https://www.example.com" required>
                    <button class="btn btn-primary" type="submit">Проверить</button>
                </div>
            </form>
        </div>
    </div>
`
)